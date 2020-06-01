package com.sergiomartinrubio.employeeshierarchyservice.service

import com.sergiomartinrubio.employeeshierarchyservice.model.Session
import com.sergiomartinrubio.employeeshierarchyservice.repository.SessionRepository
import com.sergiomartinrubio.employeeshierarchyservice.utils.AuthorizationTokenUtils
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class SessionService(private val sessionRepository: SessionRepository,
                     private val authorizationTokenUtils: AuthorizationTokenUtils) {

    fun saveSession(): String {
        val token = authorizationTokenUtils.issueToken()
        val localDateTime = LocalDateTime.now()
        sessionRepository.save(Session(token, localDateTime))
        return token
    }

    fun isValidSession(token: String): Boolean {
        val session = sessionRepository.findById(token)

        // token is not valid after 60 seconds
        if (session.isEmpty || session.get().creationDateTime.plusSeconds(60L).isBefore(LocalDateTime.now())) {
            return false
        }
        return true
    }

}