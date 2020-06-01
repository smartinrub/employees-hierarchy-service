package com.sergiomartinrubio.employeeshierarchyservice.controller

import com.sergiomartinrubio.employeeshierarchyservice.model.Credentials
import com.sergiomartinrubio.employeeshierarchyservice.service.CredentialsService
import com.sergiomartinrubio.employeeshierarchyservice.service.SessionService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    private val AUTHORIZATION_TOKEN = "YMKXuwGFC_sFZB-sreygU9zMSYjte_sH"

    @MockBean
    private lateinit var credentialsService: CredentialsService

    @MockBean
    private lateinit var sessionService: SessionService

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun givenValidCredentialsWhenAuthenticatePostRequestThenReturnOkayStatusAndToken() {
        // GIVEN
        val requestBody = "{\"username\": \"admin\", \"password\": \"secret\"}"
        given(credentialsService.isValidCredentials(Credentials("admin", "secret")))
                .willReturn(true)
        given(sessionService.saveSession()).willReturn(AUTHORIZATION_TOKEN)

        // WHEN
        val result = mvc.perform(MockMvcRequestBuilders.post("/authenticate")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()

        // THEN
        Assertions.assertThat(result.response.contentAsString).isEqualTo(AUTHORIZATION_TOKEN)
    }

    @Test
    fun givenInvalidCredentialsWhenAuthenticatePostRequestThenReturnForbidden() {
        // GIVEN
        val requestBody = "{\"username\": \"admin\", \"password\": \"secret\"}"
        given(credentialsService.isValidCredentials(Credentials("admin", "secret")))
                .willReturn(false)

        // WHEN
        mvc.perform(MockMvcRequestBuilders.post("/authenticate")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden)
                .andReturn()

        // THEN
        then(sessionService).shouldHaveNoInteractions()
    }


}