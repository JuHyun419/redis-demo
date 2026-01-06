package jh.redisexample.domain.lua

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "strategy set", description = "strategy api")
@RestController
@RequestMapping("/api/v1/strategy")
class StrategyController(
    private val service: RedisStrategy,
) {
    @GetMapping("/lua-script")
    fun luaScript(
        @RequestParam @Valid key1: String,
        @RequestParam @Valid key2: String,
        @RequestParam @Valid newKey: String,
    ) {
        service.luaScript(key1, key2, newKey)
    }
}
