package com.jipsa.balearn.common.constants

object BalearnConstants {
    const val BAD_REQUEST: Int = 400
    const val UNAUTHORIZED: Int = 401
    const val FORBIDDEN: Int = 403
    const val NOT_FOUND: Int = 404
    const val CONFLICT: Int = 409
    const val INTERNAL_SERVER: Int = 900

    const val BEARER: String = "Bearer "

    const val AUTHORIZATION_HEADER: String = "Authorization"

    const val REFRESH_TOKEN: String = "refreshToken"

    const val LOGIN_TOKEN: String = "loginToken"

    const val TEAM_INVITE_TIME: Long = 1000 * 60 * 60 * 24 // 1일

    const val CHAT_TOPIC: String = "CHATTING"
}
