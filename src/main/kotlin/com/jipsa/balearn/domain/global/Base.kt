package com.jipsa.balearn.domain.global

import com.jipsa.balearn.domain.user.UserId
import java.time.LocalDateTime

abstract class Base (
    var createdBy: UserId? = null,
    var createdAt: LocalDateTime? = null,
    var modifiedBy: UserId? = null,
    var modifiedAt: LocalDateTime? = null
)