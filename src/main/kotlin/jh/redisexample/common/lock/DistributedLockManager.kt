package jh.redisexample.common.lock

import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * 분산락 관리자
 *
 * 분산 환경(여러 서버)에서 동시에 같은 자원에 접근하는 것을 방지하기 위한 락 매니저
 * Redisson 라이브러리를 사용하여 Redis 기반 분산락 구현
 */
@Component
class DistributedLockManager(
    private val redissonClient: RedissonClient
) {
    companion object {
        private const val LOCK_PREFIX = "lock:"
        private const val DEFAULT_WAIT_TIME = 10L      // 락 획득 대기 시간 (초)
        private const val DEFAULT_LEASE_TIME = 30L     // 락 유지 시간 (초)
    }

    /**
     * 락 획득 시도
     *
     * @param key 락 키 (자원 식별자)
     * @param waitTime 락 획득 대기 시간 (초)
     * @param leaseTime 락 유지 시간 (초)
     * @return 락 획득 성공 여부
     */
    fun tryLock(
        key: String,
        waitTime: Long = DEFAULT_WAIT_TIME,
        leaseTime: Long = DEFAULT_LEASE_TIME
    ): Boolean {
        val lock = redissonClient.getLock("$LOCK_PREFIX$key")
        return try {
            lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            false
        }
    }

    /**
     * 락 해제
     *
     * @param key 락 키
     */
    fun unlock(key: String) {
        val lock = redissonClient.getLock("$LOCK_PREFIX$key")
        if (lock.isHeldByCurrentThread) {
            lock.unlock()
        }
    }

    fun unlock(lock: RLock) {
        if (lock.isHeldByCurrentThread) {
            lock.unlock()
        }
    }

    /**
     * 락을 획득하고 작업 수행 후 자동 해제
     *
     * @param key 락 키
     * @param waitTime 락 획득 대기 시간 (초)
     * @param leaseTime 락 유지 시간 (초)
     * @param action 락 획득 후 수행할 작업
     * @return 작업 결과 (락 획득 실패 시 null)
     */
    fun <T> executeWithLock(
        key: String,
        waitTime: Long = DEFAULT_WAIT_TIME,
        leaseTime: Long = DEFAULT_LEASE_TIME,
        action: () -> T
    ): T? {
        val lock = redissonClient.getLock("$LOCK_PREFIX$key")

        return try {
            val acquired = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS)

            if (acquired) {
                try {
                    action()
                } finally {
                    unlock(lock)
                }
            } else {
                null
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            null
        }
    }

    /**
     * 락을 획득하고 작업 수행 (실패 시 예외 발생)
     *
     * @param key 락 키
     * @param waitTime 락 획득 대기 시간 (초)
     * @param leaseTime 락 유지 시간 (초)
     * @param action 락 획득 후 수행할 작업
     * @return 작업 결과
     * @throws LockAcquisitionException 락 획득 실패 시
     */
    fun <T> executeWithLockOrThrow(
        key: String,
        waitTime: Long = DEFAULT_WAIT_TIME,
        leaseTime: Long = DEFAULT_LEASE_TIME,
        action: () -> T
    ): T {
        return executeWithLock(key, waitTime, leaseTime, action)
            ?: throw LockAcquisitionException("락 획득 실패: $key")
    }

    /**
     * 현재 락이 걸려있는지 확인
     *
     * @param key 락 키
     * @return 락 여부
     */
    fun isLocked(key: String): Boolean {
        val lock = redissonClient.getLock("$LOCK_PREFIX$key")
        return lock.isLocked
    }

    /**
     * 현재 스레드가 락을 보유하고 있는지 확인
     *
     * @param key 락 키
     * @return 현재 스레드의 락 보유 여부
     */
    fun isHeldByCurrentThread(key: String): Boolean {
        val lock = redissonClient.getLock("$LOCK_PREFIX$key")
        return lock.isHeldByCurrentThread
    }
}

/**
 * 락 획득 실패 예외
 */
class LockAcquisitionException(message: String) : RuntimeException(message)
