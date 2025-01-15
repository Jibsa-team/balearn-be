package com.jipsa.balearn.database.team_user.repository

import com.jipsa.balearn.database.team.entity.TeamEntity
import com.jipsa.balearn.database.team_user.entity.TeamUserEntity
import com.jipsa.balearn.domain.team.Team
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team.TeamInfo
import com.jipsa.balearn.domain.team_user.TeamUser
import com.jipsa.balearn.domain.team_user.TeamUserId
import com.jipsa.balearn.domain.team_user.TeamUserRepository
import com.jipsa.balearn.domain.user.UserId
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.stereotype.Repository
import kotlin.jvm.optionals.getOrNull

@Repository
class TeamUserRepositoryAdaptor(
    private val teamUserJpaRepository: TeamUserJpaRepository
) : TeamUserRepository {
    override fun save(teamUser: TeamUser): TeamUser {
        return teamUserJpaRepository.save(TeamUserEntity.from(teamUser)).toDomain()
    }

    override fun saveAll(teamUsers: List<TeamUser>): List<TeamUser> {
        return teamUserJpaRepository.saveAll(teamUsers.map { TeamUserEntity.from(it) }).map { it.toDomain() }
    }

    override fun findByTeamId(teamId: TeamId): List<TeamUser> {
        return teamUserJpaRepository.findByTeam_Id(teamId.value).map { it.toDomain() }
    }

    override fun findByUserId(userId: UserId): List<TeamUser> {
        return teamUserJpaRepository.findByUser_Id(userId.value).map { it.toDomain() }
    }

    override fun findById(teamUserId: TeamUserId): TeamUser? {
        return teamUserJpaRepository.findById(teamUserId.value).getOrNull()?.toDomain()
    }

    override fun findByTeamIdAndUserId(teamId: TeamId, userId: UserId): TeamUser? {
        return teamUserJpaRepository.findByTeam_IdAndUser_Id(teamId.value, userId.value)?.toDomain()
    }

    override fun existsByTeamIdAndUserId(teamId: TeamId, userId: UserId): Boolean {
        return teamUserJpaRepository.existsByTeam_IdAndUser_Id(teamId.value, userId.value)
    }

    override fun delete(teamUser: TeamUser) {
        teamUserJpaRepository.delete(TeamUserEntity.from(teamUser))
    }

    override fun deleteByTeamId(teamId: TeamId) {
        teamUserJpaRepository.deleteByTeam_Id(teamId.value)
    }

    override fun deleteByUserId(userId: UserId) {
        teamUserJpaRepository.deleteByUser_Id(userId.value)
    }

    override fun deleteById(teamUserId: TeamUserId) {
        teamUserJpaRepository.deleteById(teamUserId.value)
    }
}