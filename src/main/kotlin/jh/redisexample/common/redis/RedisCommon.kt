package jh.redisexample.common.redis

import com.google.gson.Gson
import jh.redisexample.domain.ValueWithTTL
import org.springframework.core.io.ClassPathResource
import org.springframework.data.redis.connection.StringRedisConnection
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Component
import java.time.Duration

@Component
final class RedisCommon(
    @PublishedApi internal val redisTemplate: RedisTemplate<String, String>,
    @PublishedApi internal val gson: Gson,
) {
    // 자주 쓰는 ops 캐싱
    val valueOps by lazy { redisTemplate.opsForValue() }
    val zSetOps by lazy { redisTemplate.opsForZSet() }
    val listOps by lazy { redisTemplate.opsForList() }
    val hashOps by lazy { redisTemplate.opsForHash<String, String>() }

    // ===== String Operations =====

    inline fun <reified T : Any> get(key: String): T? =
        valueOps.get(key)?.let { gson.fromJson(it, T::class.java) }

    fun <T : Any> set(key: String, value: T, ttl: Duration = Duration.ofMinutes(1)) {
        valueOps.set(key, gson.toJson(value), ttl)
    }

    fun <T : Any> multiSet(data: Map<String, T>) {
        valueOps.multiSet(data.mapValues { gson.toJson(it.value) })
    }

    // ===== Sorted Set Operations (기본) =====

    fun <T : Any> addToSortedSet(key: String, value: T, score: Double) {
        zSetOps.add(key, gson.toJson(value), score)
    }

    inline fun <reified T : Any> rangeByScore(key: String, minScore: Double, maxScore: Double): Set<T> =
        zSetOps.rangeByScore(key, minScore, maxScore)
            ?.mapTo(linkedSetOf()) { gson.fromJson(it, T::class.java) }
            ?: emptySet()

    inline fun <reified T : Any> getTopN(key: String, n: Int): Set<T> =
        zSetOps.reverseRange(key, 0, (n - 1).toLong())
            ?.mapTo(linkedSetOf()) { gson.fromJson(it, T::class.java) }
            ?: emptySet()

    // ===== Sorted Set Operations (심화) =====

    /**
     * 특정 멤버의 순위 조회 (높은 점수가 1위)
     * @return 순위 (0부터 시작), 없으면 null
     */
    fun <T : Any> getRank(key: String, value: T): Long? =
        zSetOps.reverseRank(key, gson.toJson(value))

    /**
     * 특정 멤버의 점수 조회
     */
    fun <T : Any> getScore(key: String, value: T): Double? =
        zSetOps.score(key, gson.toJson(value))

    /**
     * 점수 증가 (음수면 감소)
     * @return 변경 후 점수
     */
    fun <T : Any> incrementScore(key: String, value: T, delta: Double): Double? =
        zSetOps.incrementScore(key, gson.toJson(value), delta)

    /**
     * 특정 멤버 삭제
     * @return 삭제된 개수
     */
    fun <T : Any> removeFromSortedSet(key: String, value: T): Long =
        zSetOps.remove(key, gson.toJson(value)) ?: 0

    /**
     * 순위 범위로 삭제 (예: 하위 10명 삭제)
     * @return 삭제된 개수
     */
    fun removeByRankRange(key: String, start: Long, end: Long): Long =
        zSetOps.removeRange(key, start, end) ?: 0

    /**
     * 점수 범위로 삭제
     * @return 삭제된 개수
     */
    fun removeByScoreRange(key: String, minScore: Double, maxScore: Double): Long =
        zSetOps.removeRangeByScore(key, minScore, maxScore) ?: 0

    /**
     * Sorted Set 멤버 수 조회
     */
    fun sortedSetSize(key: String): Long =
        zSetOps.size(key) ?: 0

    /**
     * 점수 범위 내 멤버 수 조회
     */
    fun countByScoreRange(key: String, minScore: Double, maxScore: Double): Long =
        zSetOps.count(key, minScore, maxScore) ?: 0

    // ===== List Operations =====

    fun <T : Any> addToListLeft(key: String, value: T) {
        listOps.leftPush(key, gson.toJson(value))
    }

    fun <T : Any> addToListRight(key: String, value: T) {
        listOps.rightPush(key, gson.toJson(value))
    }

    inline fun <reified T : Any> getAllList(key: String): List<T> =
        listOps.range(key, 0, -1)
            ?.map { gson.fromJson(it, T::class.java) }
            ?: emptyList()

    fun <T : Any> removeFromList(key: String, value: T, count: Long = 1) {
        listOps.remove(key, count, gson.toJson(value))
    }

    // ===== Hash Operations (기본) =====

    fun <T : Any> putInHash(key: String, field: String, value: T) {
        hashOps.put(key, field, gson.toJson(value))
    }

    inline fun <reified T : Any> getFromHash(key: String, field: String): T? =
        hashOps.get(key, field)?.let { gson.fromJson(it, T::class.java) }

    fun removeFromHash(key: String, vararg fields: String) {
        hashOps.delete(key, *fields)
    }

    // ===== Hash Operations (심화) =====

    /**
     * 여러 필드 한번에 저장
     */
    fun <T : Any> putAllInHash(key: String, data: Map<String, T>) {
        val jsonMap = data.mapValues { gson.toJson(it.value) }
        hashOps.putAll(key, jsonMap)
    }

    /**
     * Hash 전체 조회
     */
    fun getAllFromHash(key: String): Map<String, String> =
        hashOps.entries(key) ?: emptyMap()

    /**
     * Hash 필드 존재 여부
     */
    fun hasFieldInHash(key: String, field: String): Boolean =
        hashOps.hasKey(key, field)

    /**
     * Hash 필드 개수
     */
    fun hashSize(key: String): Long =
        hashOps.size(key)

    /**
     * Hash 모든 필드명 조회
     */
    fun getHashFields(key: String): Set<String> =
        hashOps.keys(key)

    /**
     * Hash 모든 값 조회
     */
    fun getHashValues(key: String): List<String> =
        hashOps.values(key)

    /**
     * Hash 필드 값 증가 (숫자 필드용)
     */
    fun incrementHashField(key: String, field: String, delta: Long): Long =
        hashOps.increment(key, field, delta)

    /**
     * Hash 필드 값 증가 (실수 필드용)
     */
    fun incrementHashField(key: String, field: String, delta: Double): Double =
        hashOps.increment(key, field, delta)

    /**
     * Bitmap
     */
    fun setBit(key: String, offset: Long, value: Boolean) {
        valueOps.setBit(key, offset, value)
    }

    fun getBit(key: String, offset: Long): Boolean =
        valueOps.getBit(key, offset) ?: false

    inline fun <reified T : Any> getValueWithTTL(key: String): ValueWithTTL<T?> {
        return try {
            val results = redisTemplate.executePipelined { connection ->
                val conn = connection as StringRedisConnection
                conn.get(key)
                conn.ttl(key)
                null
            }

            val value = (results[0] as? String)?.let { gson.fromJson(it, T::class.java) }
            val ttl = results[1] as? Long

            ValueWithTTL(value, ttl)
        } catch (e: Exception) {
            e.printStackTrace()
            ValueWithTTL(null, null)
        }
    }

    fun sumTwoKeyAndRenew(key1: String, key2: String, resultKey: String): Long? {
        val redisScript = DefaultRedisScript<Long>().apply {
            setLocation(ClassPathResource("/lua/newKey.lua"))
            resultType = Long::class.java
        }

        val keys = listOf(key1, key2, resultKey)

        return redisTemplate.execute(redisScript, keys)
    }
}
