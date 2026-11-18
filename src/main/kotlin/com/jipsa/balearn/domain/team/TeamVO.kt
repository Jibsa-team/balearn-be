package com.jipsa.balearn.domain.team

import com.fasterxml.jackson.annotation.JsonValue

@JvmInline
value class TeamId(@JsonValue val value: Long = 0)

data class TeamInfo(
    val name: String,
    val description: String,
    val teamImageUrl: String = "/icons/logo_bg.png"
)
