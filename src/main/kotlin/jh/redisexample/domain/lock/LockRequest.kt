package jh.redisexample.domain.lock

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

@Schema(description = "상품 생성 요청")
data class ProductCreateRequest(
    @Schema(description = "상품 ID")
    @field:NotBlank
    val productId: String,

    @Schema(description = "상품명")
    @field:NotBlank
    val name: String,

    @Schema(description = "재고")
    @field:Min(0)
    val stock: Int,

    @Schema(description = "가격")
    @field:Min(0)
    val price: Long
)

@Schema(description = "재고 차감 요청")
data class StockDecreaseRequest(
    @Schema(description = "상품 ID")
    @field:NotBlank
    val productId: String,

    @Schema(description = "차감 수량")
    @field:Min(1)
    val quantity: Int
)

@Schema(description = "동시성 테스트 요청")
data class ConcurrencyTestRequest(
    @Schema(description = "상품 ID")
    @field:NotBlank
    val productId: String,

    @Schema(description = "동시 요청 수")
    @field:Min(1)
    val concurrentRequests: Int = 10,

    @Schema(description = "각 요청당 차감 수량")
    @field:Min(1)
    val quantityPerRequest: Int = 1
)
