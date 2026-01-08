package jh.redisexample.domain.lock

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "상품 재고 모델")
data class Product(
    val productId: String,
    val name: String,
    var stock: Int,
    var price: Long
)

@Schema(description = "락 테스트 결과")
data class LockTestResult(
    @Schema(description = "성공 여부")
    val success: Boolean,

    @Schema(description = "메시지")
    val message: String,

    @Schema(description = "처리 시간 (ms)")
    val processingTime: Long? = null,

    @Schema(description = "결과 데이터")
    val data: Any? = null
)

@Schema(description = "재고 차감 결과")
data class StockDecreaseResult(
    val productId: String,
    val previousStock: Int,
    val decreasedAmount: Int,
    val currentStock: Int
)
