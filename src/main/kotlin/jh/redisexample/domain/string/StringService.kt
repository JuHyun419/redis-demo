package jh.redisexample.domain.string

import jh.redisexample.common.redis.RedisCommon
import org.springframework.stereotype.Service

@Service
class StringService(
    private val redis: RedisCommon
) {
    fun set(request: StringRequest) {
        val key = request.baseRequest.key
        val stringModel = StringModel(key, request.value)

        redis.set(key, stringModel)
    }

    fun get(key: String): StringResponse {
        val result: StringModel? = redis.get(key)
        val response = arrayListOf<StringModel>()

        result?.let { response.add(it) }

        return StringResponse(response)
    }

    fun multiSet(request: MultiStringRequest) {
        val dataMap = request.names.mapIndexed { index, name ->
            val key = "key:${index + 1}"
            key to StringModel(key, name)
        }.toMap()

        redis.multiSet(dataMap)
    }
}
