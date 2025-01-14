package com.jipsa.balearn.domain.team

import com.jipsa.balearn.common.dto.File
import com.jipsa.balearn.domain.team_goal.TeamGoal
import com.jipsa.balearn.domain.team_goal.TeamGoalAppender
import com.jipsa.balearn.domain.team_goal.TeamGoalInfo
import com.jipsa.balearn.domain.team_user.TeamUser
import com.jipsa.balearn.domain.team_user.TeamUserAppender
import com.jipsa.balearn.domain.team_user.TeamUserRole
import com.jipsa.balearn.domain.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TeamService(
    private val teamAppender: TeamAppender,
    private val teamReader: TeamReader,
    private val teamGoalAppender: TeamGoalAppender,
    private val teamUserAppender: TeamUserAppender,
    private val teamImageAppender: TeamImageAppender
) {
    @Transactional
    fun createTeam(name: String, description: String, goals: List<TeamGoalInfo>?, image: File?, user: User): Team {

        val teamInfo = teamImageAppender.append(image)?.let {
            TeamInfo(
                name = name,
                description = description,
                teamImageUrl = it
            )
        } ?: TeamInfo(
            name = name,
            description = description
        )

        val team = teamAppender.append(
            Team(
                _teamInfo = teamInfo
            )
        )

        goals?.let { goalInfos ->
            teamGoalAppender.appendAll(goalInfos.map {
                TeamGoal(
                    team = team,
                    _teamGoalInfo = it
                )
            })
        }

        teamUserAppender.append(
            TeamUser.from(
                team = team,
                user = user,
                role = TeamUserRole.OWNER
            )
        )

        return team
    }
}