package jh.redisexample.domain

data class ValueWithTTL<T>(
    val value: T,
    val ttl: Long?
)
