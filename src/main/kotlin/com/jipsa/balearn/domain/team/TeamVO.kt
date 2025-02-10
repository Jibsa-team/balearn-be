package com.jipsa.balearn.domain.team

import com.fasterxml.jackson.annotation.JsonValue

@JvmInline
value class TeamId(@JsonValue val value: Long = 0)

data class TeamInfo(
    val name: String,
    val description: String,
    val teamImageUrl: String = "https://cdn.balearn.o-r.kr/profile/default-team.png"
)