package jh.redisexample.domain.lua

import jh.redisexample.common.redis.RedisCommon
import jh.redisexample.domain.string.StringModel
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import kotlin.math.exp

@Service
class RedisStrategy(
    private val redis: RedisCommon,
) {
    fun perStrategy(key: String): StringModel {
        val valueWithTTL = redis.getValueWithTTL<StringModel>(key)

        return if (valueWithTTL.value != null) {
            // 비동기로 확률적 갱신
            asyncPERStrategy(key, valueWithTTL.ttl ?: 0)
            valueWithTTL.value
        } else {
            // DB 조회 후 캐시 저장
            val fromDB = StringModel(key, "new db")
            redis.set(key, fromDB)
            fromDB
        }
    }

    @Async
    fun asyncPERStrategy(key: String, remainTTL: Long) {
        val probability = calculateProbability(remainTTL)

        if (Math.random() < probability) {
            val fromDB = StringModel(key, "db from")
            redis.set(key, fromDB)
        }
    }

    private fun calculateProbability(remainTTL: Long): Double {
        val base = 0.5
        val decayRate = 0.1
        return base * exp(-decayRate * remainTTL)
    }

    fun luaScript(key1: String, key2: String, newKey: String) {
        redis.sumTwoKeyAndRenew(key1, key2, newKey)
    }
}
