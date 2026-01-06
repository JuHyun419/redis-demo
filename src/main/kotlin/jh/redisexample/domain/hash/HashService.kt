package jh.redisexample.domain.hash

import jh.redisexample.common.redis.RedisCommon
import org.springframework.stereotype.Service

@Service
class HashService(
    private val redis: RedisCommon
) {
    // ===== 기본 기능 =====

    /**
     * Hash에 단일 필드 저장 (문자열)
     */
    fun put(request: HashPutRequest) {
        redis.putInHash(request.baseRequest.key, request.field, request.value)
    }

    /**
     * Hash에 사용자 프로필 저장 (객체)
     */
    fun putUserProfile(request: HashUserProfileRequest) {
        redis.putInHash(request.baseRequest.key, request.field, request.profile)
    }

    /**
     * Hash에서 필드 조회 (문자열)
     */
    fun get(key: String, field: String): HashFieldResponse<String> {
        val value: String? = redis.getFromHash(key, field)
        return HashFieldResponse(key, field, value)
    }

    /**
     * Hash에서 사용자 프로필 조회 (객체)
     */
    fun getUserProfile(key: String, field: String): HashFieldResponse<UserProfile> {
        val value: UserProfile? = redis.getFromHash(key, field)
        return HashFieldResponse(key, field, value)
    }

    /**
     * Hash 필드 삭제
     */
    fun remove(key: String, fields: List<String>): Long {
        if (fields.isEmpty()) return 0
        redis.removeFromHash(key, *fields.toTypedArray())
        return fields.size.toLong()
    }

    // ===== 심화 기능 =====

    /**
     * 여러 필드 한번에 저장
     */
    fun putAll(request: HashMultiPutRequest) {
        val data = request.fields.associate { it.field to it.value }
        redis.putAllInHash(request.baseRequest.key, data)
    }

    /**
     * Hash 전체 조회
     */
    fun getAll(key: String): HashAllResponse {
        val entries = redis.getAllFromHash(key)
        return HashAllResponse(key, entries)
    }

    /**
     * Hash 통계 조회
     */
    fun getStats(key: String): HashStatsResponse {
        val fieldCount = redis.hashSize(key)
        val fields = redis.getHashFields(key).toList()
        return HashStatsResponse(key, fieldCount, fields)
    }

    /**
     * 필드 존재 여부 확인
     */
    fun hasField(key: String, field: String): Boolean {
        return redis.hasFieldInHash(key, field)
    }

    /**
     * 숫자 필드 증가
     */
    fun incrementField(key: String, field: String, delta: Long): Long {
        return redis.incrementHashField(key, field, delta)
    }
}
