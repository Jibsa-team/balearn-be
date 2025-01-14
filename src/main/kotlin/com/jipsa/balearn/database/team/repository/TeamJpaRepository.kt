package com.jipsa.balearn.database.team.repository

import com.jipsa.balearn.database.team.entity.TeamEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TeamJpaRepository : JpaRepository<TeamEntity, Long> {

}