package jh.redisexample.domain.sortedset

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jh.redisexample.common.request.BaseRequest

@Schema(description = "Sorted Set 추가 요청")
data class SortedSetAddRequest(
    val baseRequest: BaseRequest,

    @Schema(description = "멤버 ID")
    @field:NotBlank
    val memberId: String,

    @Schema(description = "멤버 이름")
    @field:NotBlank
    val name: String,

    @Schema(description = "점수")
    val score: Double
)

@Schema(description = "Sorted Set 범위 조회 요청")
data class SortedSetRangeRequest(
    val baseRequest: BaseRequest,

    @Schema(description = "최소 점수")
    val minScore: Double,

    @Schema(description = "최대 점수")
    val maxScore: Double
)

@Schema(description = "Sorted Set Top N 조회 요청")
data class SortedSetTopNRequest(
    val baseRequest: BaseRequest,

    @Schema(description = "조회할 개수")
    val n: Int
)
