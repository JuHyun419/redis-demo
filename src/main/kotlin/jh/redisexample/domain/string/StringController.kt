package jh.redisexample.domain.string

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name ="string ", description = "string api")
@RestController
@RequestMapping("/api/v1")
class StringController(
    val service: StringService,
) {
    @Operation(summary = "set string")
    @PostMapping("/string")
    fun setString(
        @RequestBody @Valid request: StringRequest
    ) {
        service.set(request)
    }

    @GetMapping("/string")
    fun getString(
        @RequestParam key: String,
    ): StringResponse {
        return service.get(key)
    }

    @PostMapping("/string/multi")
    fun multiString(
        @RequestBody @Valid request: MultiStringRequest
    ) {
        service.multiSet(request)
    }
}
