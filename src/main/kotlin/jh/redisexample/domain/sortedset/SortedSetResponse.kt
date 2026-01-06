package jh.redisexample.domain.sortedset

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Sorted Set 응답")
data class SortedSetResponse(
    @Schema(description = "결과 목록")
    val members: List<SortedSetModel>
)

@Schema(description = "순위 응답")
data class RankResponse(
    @Schema(description = "멤버 정보")
    val member: SortedSetModel,

    @Schema(description = "순위 (1부터 시작, null이면 존재하지 않음)")
    val rank: Long?
)

@Schema(description = "점수 변경 응답")
data class ScoreResponse(
    @Schema(description = "멤버 ID")
    val memberId: String,

    @Schema(description = "이전 점수")
    val previousScore: Double,

    @Schema(description = "변경 후 점수")
    val newScore: Double?
)

@Schema(description = "Sorted Set 통계 응답")
data class SortedSetStatsResponse(
    @Schema(description = "키")
    val key: String,

    @Schema(description = "전체 멤버 수")
    val totalCount: Long
)
