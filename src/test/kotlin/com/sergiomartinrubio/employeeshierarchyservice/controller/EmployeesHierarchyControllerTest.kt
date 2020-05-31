package com.sergiomartinrubio.employeeshierarchyservice.controller

import com.sergiomartinrubio.employeeshierarchyservice.exception.EmployeeNotFoundException
import com.sergiomartinrubio.employeeshierarchyservice.exception.InvalidInputException
import com.sergiomartinrubio.employeeshierarchyservice.model.Employee
import com.sergiomartinrubio.employeeshierarchyservice.service.EmployeesHierarchyService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
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
class EmployeesHierarchyControllerTest {

    @MockBean
    private lateinit var employeesHierarchyService: EmployeesHierarchyService

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun givenCorrectBodyWhenPostRequestThenReturnCorrectResponseBodyAndNoContentStatus() {
        // GIVEN
        val requestBody = "{\"Pete\": \"Nick\", \"Barbara\": \"Nick\", \"Nick\": \"Sophie\", \"Sophie\": \"Jonas\"}"
        val responseBody = "{\"Jonas\": { \"Sophie\": {\"Nick\": { \"Pete\": {},\"Barbara\": {} }} }}"
        given(employeesHierarchyService.processHierarchy(requestBody)).willReturn(responseBody)

        // WHEN
        val result = mvc.perform(MockMvcRequestBuilders.post("/employees")
                .contentType(MediaType.TEXT_PLAIN)
                .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andReturn()

        // THEN
        Assertions.assertThat(result.response.contentAsString).isEqualTo(responseBody)
    }

    @Test
    fun givenWrongBodyWhenPostRequestThenReturnCorrectResponseBodyAndBadRequest() {
        // GIVEN
        val requestBody = "Invalid json"
        given(employeesHierarchyService.processHierarchy(requestBody))
                .willThrow(InvalidInputException("Invalid body"))

        // WHEN
        val result = mvc.perform(MockMvcRequestBuilders.post("/employees")
                .contentType(MediaType.TEXT_PLAIN)
                .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andReturn()

        // THEN
        Assertions.assertThat(result.response.contentAsString).isEqualTo("Invalid body")
    }

    @Test
    fun givenEmployeeNameWhenGetSupervisorRequestThenReturnSupervisorWithSupervisorsSupervisorName() {
        // GIVEN
        val employeeName = "Nick"
        val supervisor = Employee("Sophie", "Jonas")
        val response = "{\"name\":\"Sophie\",\"supervisor\":\"Jonas\"}"
        given(employeesHierarchyService.getSupervisor(employeeName)).willReturn(supervisor)

        // WHEN
        val result = mvc.perform(MockMvcRequestBuilders.get("/employees/{name}/supervisor", employeeName))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()

        // THEN
        Assertions.assertThat(result.response.contentAsString).isEqualTo(response)
    }

    @Test
    fun givenEmployeeNotFoundWhenGetSupervisorRequestThenReturnNotFoundResponse() {
        // GIVEN
        val employeeName = "Nick"
        given(employeesHierarchyService.getSupervisor(employeeName))
                .willThrow(EmployeeNotFoundException("Employee not found"))

        // WHEN
        val result = mvc.perform(MockMvcRequestBuilders.get("/employees/{name}/supervisor", employeeName))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andReturn()

        // THEN
        Assertions.assertThat(result.response.contentAsString).isEqualTo("Employee not found")
    }
}