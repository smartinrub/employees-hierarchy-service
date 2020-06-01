package com.sergiomartinrubio.employeeshierarchyservice.service

import com.sergiomartinrubio.employeeshierarchyservice.exception.AuthenticationException
import com.sergiomartinrubio.employeeshierarchyservice.model.Credentials
import com.sergiomartinrubio.employeeshierarchyservice.repository.CredentialsRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class CredentialsServiceTest {

    @Mock
    private lateinit var credentialsRepository: CredentialsRepository

    @InjectMocks
    private lateinit var credentialsService: CredentialsService

    @Test
    fun givenValidCredentialsWhenIsValidCredentialsThenReturnTrue() {
        // GIVEN
        val credentials = Credentials("admin", "secret")
        given(credentialsRepository.findById("admin")).willReturn(Optional.of(credentials))

        // WHEN
        val isValidCredentialsResult = credentialsService.isValidCredentials(credentials)

        // THEN
        assertThat(isValidCredentialsResult).isTrue()
    }

    @Test
    fun givenNotFoundCredentialsWhenIsValidCredentialsThenThrowAuthenticationException() {
        // GIVEN
        val credentials = Credentials("admin", "secret")
        given(credentialsRepository.findById("admin")).willReturn(Optional.empty())

        // WHEN
        // THEN
        assertThatThrownBy { credentialsService.isValidCredentials(credentials) }
                .isInstanceOf(AuthenticationException::class.java)
    }
}