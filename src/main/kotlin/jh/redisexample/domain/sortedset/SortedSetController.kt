package jh.redisexample.domain.sortedset

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@Tag(name = "sorted-set", description = "Sorted Set API")
@RestController
@RequestMapping("/api/v1/sorted-set")
class SortedSetController(
    private val service: SortedSetService
) {
    // ===== 기본 기능 =====

    @Operation(summary = "Sorted Set에 멤버 추가")
    @PostMapping
    fun add(
        @RequestBody @Valid request: SortedSetAddRequest
    ) {
        service.add(request)
    }

    @Operation(summary = "점수 범위로 조회")
    @GetMapping("/range")
    fun getRangeByScore(
        @RequestParam key: String,
        @RequestParam minScore: Double,
        @RequestParam maxScore: Double
    ): SortedSetResponse {
        return service.getRangeByScore(key, minScore, maxScore)
    }

    @Operation(summary = "상위 N개 조회 (점수 높은 순)")
    @GetMapping("/top")
    fun getTopN(
        @RequestParam key: String,
        @RequestParam n: Int
    ): SortedSetResponse {
        return service.getTopN(key, n)
    }

    // ===== 심화 기능 =====

    @Operation(summary = "특정 멤버의 순위 조회")
    @GetMapping("/rank")
    fun getRank(
        @RequestParam key: String,
        @RequestParam memberId: String,
        @RequestParam name: String,
        @RequestParam score: Double
    ): RankResponse {
        return service.getRank(key, memberId, name, score)
    }

    @Operation(summary = "점수 증가/감소")
    @PatchMapping("/score")
    fun incrementScore(
        @RequestParam key: String,
        @RequestParam memberId: String,
        @RequestParam name: String,
        @RequestParam currentScore: Double,
        @RequestParam delta: Double
    ): ScoreResponse {
        return service.incrementScore(key, memberId, name, currentScore, delta)
    }

    @Operation(summary = "멤버 삭제")
    @DeleteMapping
    fun remove(
        @RequestParam key: String,
        @RequestParam memberId: String,
        @RequestParam name: String,
        @RequestParam score: Double
    ): Map<String, Long> {
        val deleted = service.remove(key, memberId, name, score)
        return mapOf("deleted" to deleted)
    }

    @Operation(summary = "Sorted Set 통계 조회")
    @GetMapping("/stats")
    fun getStats(
        @RequestParam key: String
    ): SortedSetStatsResponse {
        return service.getStats(key)
    }

    @Operation(summary = "점수 범위 내 멤버 수 조회")
    @GetMapping("/count")
    fun countByScoreRange(
        @RequestParam key: String,
        @RequestParam minScore: Double,
        @RequestParam maxScore: Double
    ): Map<String, Long> {
        val count = service.countByScoreRange(key, minScore, maxScore)
        return mapOf("count" to count)
    }
}
