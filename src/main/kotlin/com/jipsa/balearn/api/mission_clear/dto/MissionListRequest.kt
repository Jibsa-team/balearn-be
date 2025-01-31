package com.jipsa.balearn.api.mission_clear.dto

data class MissionListRequest(
    val createIds: List<Long>?,
    val deleteIds: List<Long>?
) {
}