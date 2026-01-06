package jh.redisexample.domain.sortedset

import jh.redisexample.common.redis.RedisCommon
import org.springframework.stereotype.Service

@Service
class SortedSetService(
    private val redis: RedisCommon
) {
    /**
     * Sorted Set에 멤버 추가
     */
    fun add(request: SortedSetAddRequest) {
        val key = request.baseRequest.key
        val model = SortedSetModel(
            memberId = request.memberId,
            name = request.name,
            score = request.score
        )
        redis.addToSortedSet(key, model, request.score)
    }

    /**
     * 점수 범위로 조회
     */
    fun getRangeByScore(key: String, minScore: Double, maxScore: Double): SortedSetResponse {
        val result: Set<SortedSetModel> = redis.rangeByScore(key, minScore, maxScore)
        return SortedSetResponse(result.toList())
    }

    /**
     * 상위 N개 조회 (점수 높은 순)
     */
    fun getTopN(key: String, n: Int): SortedSetResponse {
        val result: Set<SortedSetModel> = redis.getTopN(key, n)
        return SortedSetResponse(result.toList())
    }

    // ===== 심화 기능 =====

    /**
     * 특정 멤버의 순위 조회
     */
    fun getRank(key: String, memberId: String, name: String, score: Double): RankResponse {
        val model = SortedSetModel(memberId, name, score)
        val rank = redis.getRank(key, model)
        return RankResponse(
            member = model,
            rank = rank?.plus(1)  // 0부터 시작하므로 +1
        )
    }

    /**
     * 점수 증가
     */
    fun incrementScore(key: String, memberId: String, name: String, currentScore: Double, delta: Double): ScoreResponse {
        val model = SortedSetModel(memberId, name, currentScore)
        val newScore = redis.incrementScore(key, model, delta)
        return ScoreResponse(
            memberId = memberId,
            previousScore = currentScore,
            newScore = newScore
        )
    }

    /**
     * 멤버 삭제
     */
    fun remove(key: String, memberId: String, name: String, score: Double): Long {
        val model = SortedSetModel(memberId, name, score)
        return redis.removeFromSortedSet(key, model)
    }

    /**
     * Sorted Set 통계 조회
     */
    fun getStats(key: String): SortedSetStatsResponse {
        val totalCount = redis.sortedSetSize(key)
        return SortedSetStatsResponse(
            key = key,
            totalCount = totalCount
        )
    }

    /**
     * 점수 범위 내 멤버 수 조회
     */
    fun countByScoreRange(key: String, minScore: Double, maxScore: Double): Long {
        return redis.countByScoreRange(key, minScore, maxScore)
    }
}
