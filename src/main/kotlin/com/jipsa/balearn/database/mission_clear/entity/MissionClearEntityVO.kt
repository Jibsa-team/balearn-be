package com.jipsa.balearn.database.mission_clear.entity

import com.jipsa.balearn.domain.mission.MissionId
import com.jipsa.balearn.domain.mission_clear.MissionClearId
import com.jipsa.balearn.domain.team_user.TeamUserId
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class MissionClearEntityId(

    @Column(name = "mission_id")
    val missionId: MissionId,

    @Column(name = "team_user_id")
    val teamUserId: TeamUserId

) : Serializable {
    fun toDomain() = MissionClearId(missionId, teamUserId)

    companion object {
        fun from(missionClearId: MissionClearId) =
            MissionClearEntityId(missionClearId.missionId, missionClearId.teamUserId)
    }
}