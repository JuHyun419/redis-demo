package jh.redisexample.domain.sortedset

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Sorted Set 멤버 모델")
data class SortedSetModel(
    @Schema(description = "멤버 ID")
    val memberId: String,

    @Schema(description = "멤버 이름")
    val name: String,

    @Schema(description = "점수")
    val score: Double
)
