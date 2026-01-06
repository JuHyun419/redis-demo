package jh.redisexample.domain.hash

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Hash 필드 모델")
data class HashFieldModel(
    @Schema(description = "필드명")
    val field: String,

    @Schema(description = "값")
    val value: String
)

@Schema(description = "사용자 프로필 모델")
data class UserProfile(
    @Schema(description = "이름")
    val name: String,

    @Schema(description = "이메일")
    val email: String,

    @Schema(description = "나이")
    val age: Int
)
