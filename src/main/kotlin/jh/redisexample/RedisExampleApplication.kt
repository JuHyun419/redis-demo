package jh.redisexample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RedisExampleApplication

fun main(args: Array<String>) {
    runApplication<RedisExampleApplication>(*args)
}
