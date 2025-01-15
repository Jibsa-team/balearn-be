package com.jipsa.balearn.api.mission.dto

import com.jipsa.balearn.domain.mission.Mission
import com.jipsa.balearn.domain.notice.Notice
import org.springframework.data.jpa.domain.AbstractAuditable_.createdBy
import java.time.LocalDateTime

data class MissionReadResponse(
    val id: Long,
    val detail: String,
    val createdAt: LocalDateTime?,
    val createdBy: Long?,
    val modifiedAt: LocalDateTime?,
    val modifiedBy: Long?
) {
    companion object {
        fun from(mission: Mission): MissionReadResponse {
            return MissionReadResponse(
                id = mission.id.value,
                detail = mission.missionInfo.detail,
                createdAt = mission.createdAt,
                createdBy = mission.createdBy?.value,
                modifiedAt = mission.modifiedAt,
                modifiedBy = mission.modifiedBy?.value
            )
        }
    }
}