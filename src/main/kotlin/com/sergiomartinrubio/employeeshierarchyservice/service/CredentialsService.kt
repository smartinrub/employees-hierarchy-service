package com.sergiomartinrubio.employeeshierarchyservice.service

import com.sergiomartinrubio.employeeshierarchyservice.exception.AuthenticationException
import com.sergiomartinrubio.employeeshierarchyservice.model.Credentials
import com.sergiomartinrubio.employeeshierarchyservice.repository.CredentialsRepository
import org.springframework.stereotype.Service

@Service
class CredentialsService(private val credentialsRepository: CredentialsRepository) {

    fun isValidCredentials(credentials: Credentials): Boolean {
        val credentialsSaved = credentialsRepository.findById(credentials.username)

        if (credentialsSaved.isEmpty) {
            throw AuthenticationException("Username ${credentials.username} not found")
        }

        if (credentials.password != credentialsSaved.get().password) {
            throw AuthenticationException("Invalid password for username ${credentials.username}")
        }

        return true
    }
}