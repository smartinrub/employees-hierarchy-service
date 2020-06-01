package com.sergiomartinrubio.employeeshierarchyservice.controller

import com.sergiomartinrubio.employeeshierarchyservice.exception.AuthenticationException
import com.sergiomartinrubio.employeeshierarchyservice.model.Credentials
import com.sergiomartinrubio.employeeshierarchyservice.service.CredentialsService
import com.sergiomartinrubio.employeeshierarchyservice.service.SessionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.WebRequest

@RestController
class AuthenticationController(private val credentialsService: CredentialsService,
                               private val sessionService: SessionService) {

    @PostMapping("/authenticate")
    fun authenticateUser(@RequestBody credentials: Credentials): ResponseEntity<String> {

        if (!credentialsService.isValidCredentials(credentials)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val token = sessionService.saveSession()
        return ResponseEntity.ok(token)
    }

    @ExceptionHandler(value = [(AuthenticationException::class)])
    fun handleAuthenticationException(ex: AuthenticationException, request: WebRequest): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
    }
}