package com.sergiomartinrubio.employeeshierarchyservice.utils

import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.*

@Component
class AuthorizationTokenUtils {

    fun issueToken(): String {
        val secureRandom = SecureRandom()
        val token = ByteArray(24)
        secureRandom.nextBytes(token)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token)
    }

}