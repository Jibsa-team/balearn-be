package com.jipsa.balearn.database.team_user.repository

import com.jipsa.balearn.database.team_user.entity.TeamUserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TeamUserJpaRepository : JpaRepository<TeamUserEntity, Long> {

    fun findByTeam_Id(teamId: Long): List<TeamUserEntity>

    fun findByUser_Id(userId: Long): List<TeamUserEntity>

    fun findByTeam_IdAndUser_Id(teamId: Long, userId: Long): TeamUserEntity?

    fun existsByTeam_IdAndUser_Id(teamId: Long, userId: Long): Boolean

    fun existsByUser_IdAndProfile_Role(userId: Long, role: String): Boolean

    fun existsByTeam_IdAndUser_IdAndProfile_Role(teamId: Long, userId: Long, role: String): Boolean

    fun deleteByTeam_Id(teamId: Long)

    fun deleteByUser_Id(userId: Long)
}