package com.sergiomartinrubio.employeeshierarchyservice.service

import com.sergiomartinrubio.employeeshierarchyservice.model.Session
import com.sergiomartinrubio.employeeshierarchyservice.repository.SessionRepository
import com.sergiomartinrubio.employeeshierarchyservice.utils.AuthorizationTokenUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class SessionServiceTest {

    private val AUTHORIZATION_TOKEN = "YMKXuwGFC_sFZB-sreygU9zMSYjte_sH"

    @Mock
    private lateinit var sessionRepository: SessionRepository

    @Mock
    private lateinit var authorizationTokenUtils: AuthorizationTokenUtils

    @InjectMocks
    private lateinit var sessionService: SessionService

    @Test
    fun givenIssuedTokenWhenSaveSessionThenTokenIsSaved() {
        // GIVEN
        given(authorizationTokenUtils.issueToken()).willReturn(AUTHORIZATION_TOKEN)

        // WHEN
        val tokenResult = sessionService.saveSession()

        // THEN
        assertThat(tokenResult).isEqualTo(AUTHORIZATION_TOKEN)
        then(sessionRepository).should().save(any())
        Mockito.verifyNoMoreInteractions(sessionRepository)
    }

    @Test
    fun givenValidSessionWhenIsValidSessionThenReturnTrue() {
        // GIVEN
        given(sessionRepository.findById(AUTHORIZATION_TOKEN))
                .willReturn(Optional.of(Session(AUTHORIZATION_TOKEN, LocalDateTime.now())))

        // WHEN
        val isValidSessionResult = sessionService.isValidSession(AUTHORIZATION_TOKEN)

        // THEN
        assertThat(isValidSessionResult).isTrue()
    }

    @Test
    fun givenNotFoundSessionWhenIsValidSessionThenReturnFalse() {
        // GIVEN
        given(sessionRepository.findById(AUTHORIZATION_TOKEN)).willReturn(Optional.empty())

        // WHEN
        val isValidSessionResult = sessionService.isValidSession(AUTHORIZATION_TOKEN)

        // THEN
        assertThat(isValidSessionResult).isFalse()
    }

    @Test
    fun givenExpiredSessionWhenIsValidSessionThenReturnFalse() {
        // GIVEN
        given(sessionRepository.findById(AUTHORIZATION_TOKEN))
                .willReturn(Optional.of(Session(AUTHORIZATION_TOKEN, LocalDateTime.now().minusSeconds(61L))))

        // WHEN
        val isValidSessionResult = sessionService.isValidSession(AUTHORIZATION_TOKEN)

        // THEN
        assertThat(isValidSessionResult).isFalse()
    }
}