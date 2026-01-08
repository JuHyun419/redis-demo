package jh.redisexample.domain.lock

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@Tag(name = "distributed-lock", description = "분산락 테스트 API")
@RestController
@RequestMapping("/api/v1/lock")
class LockController(
    private val service: LockService
) {
    // ===== 상품 관리 =====

    @Operation(summary = "테스트용 상품 생성")
    @PostMapping("/product")
    fun createProduct(
        @RequestBody @Valid request: ProductCreateRequest
    ): Product {
        return service.createProduct(request)
    }

    @Operation(summary = "상품 조회")
    @GetMapping("/product")
    fun getProduct(
        @RequestParam productId: String
    ): Product? {
        return service.getProduct(productId)
    }

    // ===== 재고 차감 (개별 테스트) =====

    @Operation(summary = "재고 차감 (락 없음) - 동시성 문제 발생!")
    @PostMapping("/stock/decrease/no-lock")
    fun decreaseStockWithoutLock(
        @RequestParam productId: String,
        @RequestParam quantity: Int
    ): StockDecreaseResult {
        return service.decreaseStockWithoutLock(productId, quantity)
    }

    @Operation(summary = "재고 차감 (분산락 사용)")
    @PostMapping("/stock/decrease/with-lock")
    fun decreaseStockWithLock(
        @RequestParam productId: String,
        @RequestParam quantity: Int
    ): StockDecreaseResult {
        return service.decreaseStockWithLock(productId, quantity)
    }

    @Operation(summary = "재고 차감 (Lua Script) - 가장 빠름!")
    @PostMapping("/stock/decrease/lua")
    fun decreaseStockWithLua(
        @RequestParam productId: String,
        @RequestParam quantity: Int
    ): StockDecreaseResult {
        return service.decreaseStockWithLua(productId, quantity)
    }

    // ===== 동시성 테스트 =====

    @Operation(summary = "동시성 테스트 (락 없음) - 문제 확인용")
    @PostMapping("/test/concurrency/no-lock")
    fun testConcurrencyWithoutLock(
        @RequestBody @Valid request: ConcurrencyTestRequest
    ): LockTestResult {
        return service.testConcurrencyWithoutLock(request)
    }

    @Operation(summary = "동시성 테스트 (분산락 사용)")
    @PostMapping("/test/concurrency/with-lock")
    fun testConcurrencyWithLock(
        @RequestBody @Valid request: ConcurrencyTestRequest
    ): LockTestResult {
        return service.testConcurrencyWithLock(request)
    }

    @Operation(summary = "동시성 테스트 (Lua Script) - 가장 빠름!")
    @PostMapping("/test/concurrency/lua")
    fun testConcurrencyWithLua(
        @RequestBody @Valid request: ConcurrencyTestRequest
    ): LockTestResult {
        return service.testConcurrencyWithLua(request)
    }

    // ===== 락 상태 확인 =====

    @Operation(summary = "락 상태 확인")
    @GetMapping("/status")
    fun getLockStatus(
        @RequestParam key: String
    ): Map<String, Any> {
        return service.getLockStatus(key)
    }
}
