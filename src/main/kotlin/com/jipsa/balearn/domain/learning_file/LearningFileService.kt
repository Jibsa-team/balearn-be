package com.jipsa.balearn.domain.learning_file

import com.jipsa.balearn.api.learning_file.dto.LearningFileResponse
import com.jipsa.balearn.common.dto.File
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team.TeamReader
import com.jipsa.balearn.domain.team_user.TeamUser
import com.jipsa.balearn.domain.team_user.TeamUserReader
import com.jipsa.balearn.domain.team_user.TeamUserValidator
import com.jipsa.balearn.domain.user.User
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LearningFileService(
    private val learningFileAppender: LearningFileAppender,
    private val fileAppender: FileAppender,
    private val teamUserReader: TeamUserReader,
    private val teamReader: TeamReader,
    private val learningFileReader: LearningFileReader,
    private val teamUserValidator: TeamUserValidator,
    private val learningFileDeleter: LearningFileDeleter
) {
    @Transactional
    fun appendLearningFile(name: String, file: File, user: User, teamId: TeamId): LearningFileResponse {
        val team = teamReader.read(teamId)
        val teamUser = teamUserReader.readBy(teamId, user.id)

        val fileUrl = fileAppender.append(file)

        val learningFile = LearningFile(
            team = team,
            _learningFileInfo = LearningFIleInfo(
                name = name,
                fileUrl = fileUrl,
                size = file.size,
                type = file.contentType
            )
        )
        return LearningFileResponse.from(
            learningFile = learningFileAppender.append(learningFile),
            createdBy = teamUser,
            modifiedBy = teamUser
        )
    }

    @Transactional
    fun deleteLearningFile(learningFileId: LearningFileId, user: User) {
        val learningFile = learningFileReader.read(learningFileId)

        try {
            teamUserValidator.validLeader(learningFile.team.id, user.id)
        } catch (e: Exception) {
            learningFile.isCreator(user.id)
        }

        learningFileDeleter.delete(learningFile)
    }

    fun readLearningFile(learningFileId: LearningFileId, user: User): LearningFileResponse {
        val learningFile = learningFileReader.read(learningFileId)
        teamUserValidator.validTeamUser(learningFile.team.id, user.id)

        return try {
            LearningFileResponse.from(
                learningFile = learningFile,
                createdBy = learningFile.createdBy?.let { teamUserReader.readBy(learningFile.team.id, it) },
                modifiedBy = learningFile.modifiedBy?.let { teamUserReader.readBy(learningFile.team.id, it) }
            )
        } catch (e: Exception) {
            LearningFileResponse.from(
                learningFile = learningFile,
                createdBy = TeamUser.ex_member(learningFile.team, user),
                modifiedBy = TeamUser.ex_member(learningFile.team, user)
            )
        }
    }

    fun readLearningFilesPage(teamId: TeamId, user: User, pageable: Pageable): List<LearningFileResponse> {
        teamUserValidator.validTeamUser(teamId, user.id)

        return learningFileReader.readAllBy(teamId, pageable).map { learningFile ->
            try {
                LearningFileResponse.from(
                    learningFile = learningFile,
                    createdBy = learningFile.createdBy?.let { teamUserReader.readBy(learningFile.team.id, it) },
                    modifiedBy = learningFile.modifiedBy?.let { teamUserReader.readBy(learningFile.team.id, it) }
                )
            } catch (e: Exception) {
                LearningFileResponse.from(
                    learningFile = learningFile,
                    createdBy = TeamUser.ex_member(learningFile.team, user),
                    modifiedBy = TeamUser.ex_member(learningFile.team, user)
                )
            }
        }
    }
}