package com.jipsa.balearn.api.mission.dto

import com.jipsa.balearn.domain.mission.MissionInfo

data class MissionUpdateRequest(
    val id: Long? = null,
    val detail: String
) {
    fun toDomain() = MissionInfo(
        detail = detail
    )
}