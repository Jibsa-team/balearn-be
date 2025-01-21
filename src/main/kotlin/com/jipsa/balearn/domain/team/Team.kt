package com.jipsa.balearn.domain.team

import com.jipsa.balearn.domain.global.Base
import com.jipsa.balearn.domain.user.UserId
import java.time.LocalDateTime

class Team(
    val id: TeamId = TeamId(),
    private var _teamInfo: TeamInfo,
    createdAt: LocalDateTime? = null,
    modifiedAt: LocalDateTime? = null,
    createdBy: UserId? = null,
    modifiedBy: UserId? = null
) : Base(
    createdAt = createdAt,
    modifiedAt = modifiedAt,
    createdBy = createdBy,
    modifiedBy = modifiedBy
) {
    val teamInfo: TeamInfo
        get() = _teamInfo

    fun updateTeamInfo(name: String?, description: String?, imgUrl: String?) {
        this._teamInfo = TeamInfo(
            name = name ?: this._teamInfo.name,
            description = description ?: this._teamInfo.description,
            teamImageUrl = imgUrl ?: this._teamInfo.teamImageUrl
        )
    }
}