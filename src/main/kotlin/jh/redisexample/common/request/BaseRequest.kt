package jh.redisexample.common.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Schema(description = "base key request")
class BaseRequest(
    @Schema(description = "key")
    @NotBlank
    @NotNull
    val key: String
)

