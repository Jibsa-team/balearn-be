package com.jipsa.balearn.common.util

import java.time.LocalDateTime
import java.time.ZoneId

fun LocalDateTime.toEpochMillis(): Double {
    return this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli().toDouble()
}