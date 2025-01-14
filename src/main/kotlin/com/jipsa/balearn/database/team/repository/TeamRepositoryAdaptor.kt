package com.jipsa.balearn.database.team.repository

import com.jipsa.balearn.database.team.entity.TeamEntity
import com.jipsa.balearn.domain.team.Team
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team.TeamRepository
import org.springframework.stereotype.Repository
import kotlin.jvm.optionals.getOrNull

@Repository
class TeamRepositoryAdaptor(
    private val teamJpaRepository: TeamJpaRepository
) : TeamRepository {
    override fun save(team: Team): Team {
        return teamJpaRepository.save(TeamEntity.from(team)).toDomain()
    }

    override fun findById(teamId: TeamId): Team? {
        return teamJpaRepository.findById(teamId.value).getOrNull()?.toDomain()
    }

    override fun delete(team: Team) {
        teamJpaRepository.delete(TeamEntity.from(team))
    }

    override fun deleteById(teamId: TeamId) {
        teamJpaRepository.deleteById(teamId.value)
    }
}