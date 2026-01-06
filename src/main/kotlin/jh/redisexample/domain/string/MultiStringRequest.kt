package jh.redisexample.domain.string

import io.swagger.v3.oas.annotations.media.Schema
import jh.redisexample.common.request.BaseRequest

class MultiStringRequest(
    val baseRequest: BaseRequest,

    @Schema(description = "names")
    val names: Array<String>
)
