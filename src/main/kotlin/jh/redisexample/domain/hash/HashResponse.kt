package jh.redisexample.domain.hash

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Hash 필드 조회 응답")
data class HashFieldResponse<T>(
    @Schema(description = "키")
    val key: String,

    @Schema(description = "필드")
    val field: String,

    @Schema(description = "값")
    val value: T?
)

@Schema(description = "Hash 전체 조회 응답")
data class HashAllResponse(
    @Schema(description = "키")
    val key: String,

    @Schema(description = "전체 필드-값")
    val entries: Map<String, String>
)

@Schema(description = "Hash 통계 응답")
data class HashStatsResponse(
    @Schema(description = "키")
    val key: String,

    @Schema(description = "필드 개수")
    val fieldCount: Long,

    @Schema(description = "필드 목록")
    val fields: List<String>
)
