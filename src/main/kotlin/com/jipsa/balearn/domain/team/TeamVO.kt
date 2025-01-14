package com.jipsa.balearn.domain.team

@JvmInline
value class TeamId(val value: Long)

data class TeamInfo(
    val name: String,
    val description: String,
    val teamImageUrl: String = "https://cdn.balearn.o-r.kr/profile/default-team.png"
)