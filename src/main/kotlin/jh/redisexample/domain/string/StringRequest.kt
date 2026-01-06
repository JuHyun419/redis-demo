package jh.redisexample.domain.string

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jh.redisexample.common.request.BaseRequest

@Schema(description = "redis string collection request")
class StringRequest(
    val baseRequest: BaseRequest,

    @Schema(description = "value")
    @NotBlank
    @NotNull
    val value: String
)
