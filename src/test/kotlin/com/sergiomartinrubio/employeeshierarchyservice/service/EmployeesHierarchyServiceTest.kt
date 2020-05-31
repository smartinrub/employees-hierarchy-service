package com.sergiomartinrubio.employeeshierarchyservice.service

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.sergiomartinrubio.employeeshierarchyservice.model.EmployeeDto
import com.sergiomartinrubio.employeeshierarchyservice.utils.EmployeeHierarchyUtils
import com.sergiomartinrubio.employeeshierarchyservice.utils.JsonUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class EmployeesHierarchyServiceTest {

    @Mock
    private lateinit var jsonUtils: JsonUtils

    @Mock
    private lateinit var employeeHierarchyUtils: EmployeeHierarchyUtils

    @InjectMocks
    private lateinit var employeesHierarchyService: EmployeesHierarchyService

    @Test
    fun void() {
        // GIVEN
        val jsonInput = "{\"Pete\": \"Nick\", \"Barbara\": \"Nick\", \"Nick\": \"Sophie\", \"Sophie\": \"Jonas\"}"
        val jsonOutput = "{\"Jonas\": { \"Sophie\": {\"Nick\": { \"Pete\": {},\"Barbara\": {} }} }}"
        val mapper = ObjectMapper(JsonFactory())
        val firstNode = createFirstNode(mapper)
        val rootEmployee = createRootEmployee()
        val employeesBySupervisorMap = createEmployeesBySupervisorMap()
        val resultEmployee = createResultEmployee()
        BDDMockito.given(jsonUtils.transformFromJsonStringToJsonNode(jsonInput)).willReturn(firstNode)
        BDDMockito.given(employeeHierarchyUtils.findRootEmployee(employeesBySupervisorMap)).willReturn(rootEmployee)
        BDDMockito.given(employeeHierarchyUtils.buildHierarchyTreeFromRootEmployee(rootEmployee, employeesBySupervisorMap))
                .willReturn(resultEmployee)
        BDDMockito.given(jsonUtils.transformFromRootEmployeeToJsonString(resultEmployee)).willReturn(jsonOutput)

        // WHEN
        val jsonResult = employeesHierarchyService.processHierarchy(jsonInput)

        // THEN
        assertThat(jsonResult).isEqualTo(jsonOutput)
        BDDMockito.then(jsonUtils).should().transformFromJsonStringToJsonNode(jsonInput)
        BDDMockito.then(employeeHierarchyUtils).should().findRootEmployee(employeesBySupervisorMap)
        BDDMockito.then(employeeHierarchyUtils).should().buildHierarchyTreeFromRootEmployee(rootEmployee, employeesBySupervisorMap)
        BDDMockito.then(jsonUtils).should().transformFromRootEmployeeToJsonString(resultEmployee)
    }

    private fun createResultEmployee(): EmployeeDto {
        return EmployeeDto(null, "Jonas",
                listOf(EmployeeDto(null, "Sophie",
                        listOf(EmployeeDto(null, "Nick",
                                listOf(EmployeeDto(null, "Pete", listOf()),
                                        EmployeeDto(null, "Barbara", listOf())))))))
    }

    private fun createEmployeesBySupervisorMap(): Map<String, List<String>> {
        return mapOf(
                "Nick" to listOf("Pete", "Barbara"),
                "Sophie" to listOf("Nick"),
                "Jonas" to listOf("Sophie")
        )
    }

    private fun createRootEmployee(): EmployeeDto {
        val rootEmployee = EmployeeDto(null, "Jonas", null)
        val employees = listOf(EmployeeDto(rootEmployee, "Sophie", listOf()))
        rootEmployee.employees = employees
        return rootEmployee
    }

    private fun createFirstNode(mapper: ObjectMapper): ObjectNode? {
        return mapper.createObjectNode()
                .put("Pete", "Nick")
                .put("Barbara", "Nick")
                .put("Nick", "Sophie")
                .put("Sophie", "Jonas")
    }
}