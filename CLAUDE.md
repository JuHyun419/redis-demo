# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

Spring Boot 3.5.9 + Kotlin 기반의 Redis 예제 프로젝트. Redis의 다양한 데이터 구조(String, Hash, Sorted Set, List, Bitmap)와 분산락, Lua 스크립트 활용법을 다룬다.

## 빌드 및 실행 명령어

```bash
./gradlew build          # 전체 빌드
./gradlew bootRun        # 애플리케이션 실행 (포트 7002)
./gradlew test           # 테스트 실행
./gradlew clean build    # 클린 빌드
```

## 아키텍처

### 계층 구조
```
Controller → Service → RedisCommon → RedisTemplate
                    ↓
         DistributedLockManager (Redisson)
```

### 핵심 컴포넌트

**RedisCommon.kt** (`src/main/kotlin/jh/redisexample/common/redis/`)
- 모든 Redis 연산을 캡슐화한 유틸리티 클래스
- Kotlin의 inline reified 제네릭으로 타입-안전한 직렬화/역직렬화
- Gson을 사용하여 객체를 JSON 문자열로 저장
- `by lazy` 위임으로 각 Ops 초기화

**DistributedLockManager.kt** (`src/main/kotlin/jh/redisexample/common/lock/`)
- Redisson 기반 분산락 관리자
- `executeWithLock()`: 람다 실행 후 자동 락 해제
- 기본 대기 10초, 락 유지 30초

### 도메인별 주요 패턴

**Lock 도메인** - 3가지 재고 차감 방식 비교:
1. 락 없음 (동시성 문제 재현용)
2. 분산락 (Redisson RLock)
3. Lua 스크립트 (원자적 연산, 가장 권장)

### Lua 스크립트

`src/main/resources/lua/` 경로에 위치:
- `decrease_stock.lua`: 원자적 재고 차감
- `newKey.lua`: 새 키 생성

## 환경 설정

- JDK 21 필수
- Redis 서버: 127.0.0.1:6379 (비밀번호: root)
- 설정 파일: `src/main/resources/application.yml`

## API 테스트

- Swagger UI: http://localhost:7002/swagger-ui.html
- HTTP 테스트 파일: `test.http` (IntelliJ HTTP Client용)
