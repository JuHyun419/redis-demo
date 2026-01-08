package jh.redisexample.domain.lock

import com.google.gson.Gson
import jh.redisexample.common.lock.DistributedLockManager
import jh.redisexample.common.redis.RedisCommon
import org.springframework.stereotype.Service
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@Service
class LockService(
    private val redis: RedisCommon,
    private val lockManager: DistributedLockManager,
    private val gson: Gson
) {
    companion object {
        private const val PRODUCT_KEY_PREFIX = "product:"
    }

    // ===== 상품 CRUD =====

    fun createProduct(request: ProductCreateRequest): Product {
        val product = Product(
            productId = request.productId,
            name = request.name,
            stock = request.stock,
            price = request.price
        )
        redis.set("$PRODUCT_KEY_PREFIX${request.productId}", product)
        return product
    }

    fun getProduct(productId: String): Product? {
        return redis.get("$PRODUCT_KEY_PREFIX$productId")
    }

    // ===== 락 없이 재고 차감 (동시성 문제 발생!) =====

    fun decreaseStockWithoutLock(productId: String, quantity: Int): StockDecreaseResult {
        val key = "$PRODUCT_KEY_PREFIX$productId"
        val product: Product = redis.get(key)
            ?: throw IllegalArgumentException("상품을 찾을 수 없습니다: $productId")

        val previousStock = product.stock

        if (product.stock < quantity) {
            throw IllegalStateException("재고 부족! 현재: ${product.stock}, 요청: $quantity")
        }

        Thread.sleep(5)

        product.stock -= quantity
        redis.set(key, product)

        return StockDecreaseResult(
            productId = productId,
            previousStock = previousStock,
            decreasedAmount = quantity,
            currentStock = product.stock
        )
    }

    // ===== 분산락 사용하여 재고 차감 =====

    fun decreaseStockWithLock(productId: String, quantity: Int): StockDecreaseResult {
        return lockManager.executeWithLockOrThrow("stock:$productId") {
            val key = "$PRODUCT_KEY_PREFIX$productId"
            val product: Product = redis.get(key)
                ?: throw IllegalArgumentException("상품을 찾을 수 없습니다: $productId")

            val previousStock = product.stock

            if (product.stock < quantity) {
                throw IllegalStateException("재고 부족! 현재: ${product.stock}, 요청: $quantity")
            }

            Thread.sleep(1)

            product.stock -= quantity
            redis.set(key, product)

            StockDecreaseResult(
                productId = productId,
                previousStock = previousStock,
                decreasedAmount = quantity,
                currentStock = product.stock
            )
        }
    }

    // ===== Lua Script로 재고 차감 (원자적 연산 - 가장 빠름!) =====

    fun decreaseStockWithLua(productId: String, quantity: Int): StockDecreaseResult {
        val key = "$PRODUCT_KEY_PREFIX$productId"
        val resultJson = redis.decreaseStockWithLua(key, quantity)
            ?: throw IllegalStateException("Lua Script 실행 실패")

        val result = gson.fromJson(resultJson, LuaScriptResult::class.java)

        if (!result.success) {
            throw IllegalStateException(result.message ?: "재고 차감 실패")
        }

        return StockDecreaseResult(
            productId = result.productId ?: productId,
            previousStock = result.previousStock ?: 0,
            decreasedAmount = result.decreasedAmount ?: quantity,
            currentStock = result.currentStock ?: 0
        )
    }

    // ===== 동시성 테스트 =====

    fun testConcurrencyWithoutLock(request: ConcurrencyTestRequest): LockTestResult {
        return runConcurrencyTest(request, "락 없음") { productId, quantity ->
            decreaseStockWithoutLock(productId, quantity)
        }
    }

    fun testConcurrencyWithLock(request: ConcurrencyTestRequest): LockTestResult {
        return runConcurrencyTest(request, "분산락 사용") { productId, quantity ->
            decreaseStockWithLock(productId, quantity)
        }
    }

    fun testConcurrencyWithLua(request: ConcurrencyTestRequest): LockTestResult {
        return runConcurrencyTest(request, "Lua Script") { productId, quantity ->
            decreaseStockWithLua(productId, quantity)
        }
    }

    private fun runConcurrencyTest(
        request: ConcurrencyTestRequest,
        testName: String,
        decreaseAction: (String, Int) -> StockDecreaseResult
    ): LockTestResult {
        val product = getProduct(request.productId)
            ?: return LockTestResult(false, "상품을 찾을 수 없습니다")

        val initialStock = product.stock
        val expectedFinalStock = initialStock - (request.concurrentRequests * request.quantityPerRequest)

        val successCount = AtomicInteger(0)
        val failCount = AtomicInteger(0)
        val latch = CountDownLatch(request.concurrentRequests)
        val executor = Executors.newFixedThreadPool(minOf(request.concurrentRequests, 100))

        val startTime = System.currentTimeMillis()

        repeat(request.concurrentRequests) {
            executor.submit {
                try {
                    decreaseAction(request.productId, request.quantityPerRequest)
                    successCount.incrementAndGet()
                } catch (e: Exception) {
                    failCount.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executor.shutdown()

        val processingTime = System.currentTimeMillis() - startTime
        val finalProduct = getProduct(request.productId)
        val actualFinalStock = finalProduct?.stock ?: 0

        return LockTestResult(
            success = actualFinalStock == expectedFinalStock,
            message = """
                |[$testName] 동시성 테스트 결과
                |초기 재고: $initialStock
                |예상 최종 재고: $expectedFinalStock
                |실제 최종 재고: $actualFinalStock
                |성공 요청: ${successCount.get()}
                |실패 요청: ${failCount.get()}
                |차이: ${actualFinalStock - expectedFinalStock} (0이어야 정상)
            """.trimMargin(),
            processingTime = processingTime,
            data = mapOf(
                "initialStock" to initialStock,
                "expectedFinalStock" to expectedFinalStock,
                "actualFinalStock" to actualFinalStock,
                "successCount" to successCount.get(),
                "failCount" to failCount.get(),
                "difference" to (actualFinalStock - expectedFinalStock)
            )
        )
    }

    // ===== 락 상태 확인 =====

    fun getLockStatus(key: String): Map<String, Any> {
        return mapOf(
            "key" to key,
            "isLocked" to lockManager.isLocked(key),
            "isHeldByCurrentThread" to lockManager.isHeldByCurrentThread(key)
        )
    }
}

// Lua Script 결과 파싱용
data class LuaScriptResult(
    val success: Boolean,
    val error: String? = null,
    val message: String? = null,
    val productId: String? = null,
    val previousStock: Int? = null,
    val decreasedAmount: Int? = null,
    val currentStock: Int? = null
)
