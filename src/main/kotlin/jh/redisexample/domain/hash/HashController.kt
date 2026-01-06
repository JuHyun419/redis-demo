package jh.redisexample.domain.hash

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@Tag(name = "hash", description = "Hash API")
@RestController
@RequestMapping("/api/v1/hash")
class HashController(
    private val service: HashService
) {
    // ===== 기본 기능 =====

    @Operation(summary = "Hash에 단일 필드 저장")
    @PostMapping
    fun put(
        @RequestBody @Valid request: HashPutRequest
    ) {
        service.put(request)
    }

    @Operation(summary = "Hash에 사용자 프로필 저장")
    @PostMapping("/profile")
    fun putUserProfile(
        @RequestBody @Valid request: HashUserProfileRequest
    ) {
        service.putUserProfile(request)
    }

    @Operation(summary = "Hash 필드 조회")
    @GetMapping
    fun get(
        @RequestParam key: String,
        @RequestParam field: String
    ): HashFieldResponse<String> {
        return service.get(key, field)
    }

    @Operation(summary = "Hash 사용자 프로필 조회")
    @GetMapping("/profile")
    fun getUserProfile(
        @RequestParam key: String,
        @RequestParam field: String
    ): HashFieldResponse<UserProfile> {
        return service.getUserProfile(key, field)
    }

    @Operation(summary = "Hash 필드 삭제")
    @DeleteMapping
    fun remove(
        @RequestParam key: String,
        @RequestParam fields: List<String>
    ): Map<String, Long> {
        val deleted = service.remove(key, fields)
        return mapOf("deleted" to deleted)
    }

    // ===== 심화 기능 =====

    @Operation(summary = "여러 필드 한번에 저장")
    @PostMapping("/multi")
    fun putAll(
        @RequestBody @Valid request: HashMultiPutRequest
    ) {
        service.putAll(request)
    }

    @Operation(summary = "Hash 전체 조회")
    @GetMapping("/all")
    fun getAll(
        @RequestParam key: String
    ): HashAllResponse {
        return service.getAll(key)
    }

    @Operation(summary = "Hash 통계 조회")
    @GetMapping("/stats")
    fun getStats(
        @RequestParam key: String
    ): HashStatsResponse {
        return service.getStats(key)
    }

    @Operation(summary = "필드 존재 여부 확인")
    @GetMapping("/exists")
    fun hasField(
        @RequestParam key: String,
        @RequestParam field: String
    ): Map<String, Boolean> {
        val exists = service.hasField(key, field)
        return mapOf("exists" to exists)
    }

    @Operation(summary = "숫자 필드 증가/감소")
    @PatchMapping("/increment")
    fun incrementField(
        @RequestParam key: String,
        @RequestParam field: String,
        @RequestParam delta: Long
    ): Map<String, Long> {
        val newValue = service.incrementField(key, field, delta)
        return mapOf("newValue" to newValue)
    }
}
