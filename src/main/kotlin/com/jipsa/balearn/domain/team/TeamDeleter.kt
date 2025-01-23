package com.jipsa.balearn.domain.team

import com.jipsa.balearn.domain.learning_file.LearningFileDeleter
import com.jipsa.balearn.domain.notice.NoticeDeleter
import com.jipsa.balearn.domain.schedule.ScheduleDeleter
import com.jipsa.balearn.domain.team_goal.TeamGoalDeleter
import com.jipsa.balearn.domain.team_user.TeamUserDeleter
import org.springframework.stereotype.Component

@Component
class TeamDeleter(
    private val teamRepository: TeamRepository,
    private val teamUserDeleter: TeamUserDeleter,
    private val teamGoalDeleter: TeamGoalDeleter,
    private val noticeDeleter: NoticeDeleter,
    private val scheduleDeleter: ScheduleDeleter,
    private val learningFileDeleter: LearningFileDeleter
) {
    fun delete(team: Team) {
        teamUserDeleter.deleteBy(team.id)
        teamGoalDeleter.deleteBy(team.id)
        noticeDeleter.deleteBy(team.id)
        scheduleDeleter.deleteBy(team.id)
        learningFileDeleter.deleteBy(team.id)

        teamRepository.delete(team)
    }

    fun delete(teamId: TeamId) {
        teamUserDeleter.deleteBy(teamId)
        teamGoalDeleter.deleteBy(teamId)
        noticeDeleter.deleteBy(teamId)
        scheduleDeleter.deleteBy(teamId)
        learningFileDeleter.deleteBy(teamId)

        teamRepository.deleteById(teamId)
    }
}