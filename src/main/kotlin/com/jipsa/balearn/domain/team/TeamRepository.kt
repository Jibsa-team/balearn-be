package com.jipsa.balearn.domain.team

interface TeamRepository {
    fun save(team: Team): Team
    fun findById(teamId: TeamId): Team?
    fun delete(team: Team)
    fun deleteById(teamId: TeamId)
}