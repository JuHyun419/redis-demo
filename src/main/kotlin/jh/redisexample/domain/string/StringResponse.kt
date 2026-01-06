package jh.redisexample.domain.string

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "redis string response")
class StringResponse(
    @Schema(description = "set string response")
    val response: List<StringModel>
)
