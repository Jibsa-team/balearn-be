package com.jipsa.balearn.domain.team_user

import com.fasterxml.jackson.annotation.JsonProperty
import com.jipsa.balearn.domain.global.Base
import com.jipsa.balearn.domain.team.Team
import com.jipsa.balearn.domain.user.User
import java.time.LocalDateTime

class TeamUser(
    val id: TeamUserId = TeamUserId(),
    private var _profile: TeamUserProfile,
    val team: Team,
    val user: User,
    createdAt: LocalDateTime? = null,
    modifiedAt: LocalDateTime? = null,
) : Base(
    createdAt = createdAt,
    modifiedAt = modifiedAt
) {
    @get:JsonProperty("_profile")
    val profile: TeamUserProfile
        get() = _profile

    fun changeRole(role: TeamUserRole) {
        this._profile = TeamUserProfile(
            nickname = this._profile.nickname,
            profileImageUrl = this._profile.profileImageUrl,
            role = role
        )
    }

    fun updateProfile(nickname: String?, profileImageUrl: String?) {
        this._profile = TeamUserProfile(
            nickname = nickname ?: this._profile.nickname,
            profileImageUrl = profileImageUrl ?: this._profile.profileImageUrl,
            role = this._profile.role
        )
    }

    fun isOwner() {
        require(this._profile.role == TeamUserRole.OWNER) { "소유자만 가능합니다." }
    }

    fun isLeader() {
        require(this._profile.role == TeamUserRole.LEADER || this._profile.role == TeamUserRole.OWNER) { "리더만 가능합니다." }
    }

    companion object {
        fun from(team: Team, user: User, role: TeamUserRole): TeamUser {
            return TeamUser(
                id = TeamUserId(0),
                _profile = TeamUserProfile(
                    nickname = user.userProfile.name,
                    profileImageUrl = user.userProfile.profileImageUrl,
                    role = role
                ),
                team = team,
                user = user
            )
        }

        fun ex_member(team: Team, user: User): TeamUser {
            return TeamUser(
                _profile = TeamUserProfile(
                    nickname = "탈퇴한 사용자",
                    profileImageUrl = "https://cdn.balearn.o-r.kr/profile/default-user.jpg",
                    role = TeamUserRole.EX_MEMBER
                ),
                team = team,
                user = user,
            )
        }
    }
}