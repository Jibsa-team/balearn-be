package com.jipsa.balearn.domain.team_user

import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.user.UserId

interface TeamUserRepository {
    fun save(teamUser: TeamUser): TeamUser
    fun saveAll(teamUsers: List<TeamUser>): List<TeamUser>
    fun findByTeamId(teamId: TeamId): List<TeamUser>
    fun findByUserId(userId: UserId): List<TeamUser>
    fun findById(teamUserId: TeamUserId): TeamUser?
    fun findByTeamIdAndUserId(teamId: TeamId, userId: UserId): TeamUser?
    fun delete(teamUser: TeamUser)
    fun deleteByTeamId(teamId: TeamId)
    fun deleteByUserId(userId: UserId)
    fun deleteById(teamUserId: TeamUserId)
    fun existsByTeamIdAndUserId(teamId: TeamId, userId: UserId): Boolean
    fun existsByUserIdAndRole(userId: UserId, role: TeamUserRole): Boolean
    fun existsByTeamIdAndUserIdAndRole(teamId: TeamId, userId: UserId, role: TeamUserRole): Boolean
}