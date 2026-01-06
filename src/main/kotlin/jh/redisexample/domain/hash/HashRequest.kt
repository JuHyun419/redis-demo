package jh.redisexample.domain.hash

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jh.redisexample.common.request.BaseRequest

@Schema(description = "Hash 필드 추가 요청")
data class HashPutRequest(
    val baseRequest: BaseRequest,

    @Schema(description = "필드명")
    @field:NotBlank
    val field: String,

    @Schema(description = "값")
    @field:NotBlank
    val value: String
)

@Schema(description = "Hash 사용자 프로필 저장 요청")
data class HashUserProfileRequest(
    val baseRequest: BaseRequest,

    @Schema(description = "필드명 (예: profile)")
    @field:NotBlank
    val field: String,

    @Schema(description = "사용자 프로필")
    val profile: UserProfile
)

@Schema(description = "Hash 다중 필드 추가 요청")
data class HashMultiPutRequest(
    val baseRequest: BaseRequest,

    @Schema(description = "필드-값 목록")
    val fields: List<HashFieldModel>
)

@Schema(description = "Hash 필드 삭제 요청")
data class HashRemoveRequest(
    val baseRequest: BaseRequest,

    @Schema(description = "삭제할 필드명 목록")
    val fields: List<String>
)
