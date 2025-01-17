package com.jipsa.balearn.api.mission.dto

import com.jipsa.balearn.domain.mission.MissionInfo

data class MissionCreateRequest(
    val detail: String
) {
    fun toDomain() = MissionInfo(
        detail = detail
    )
}