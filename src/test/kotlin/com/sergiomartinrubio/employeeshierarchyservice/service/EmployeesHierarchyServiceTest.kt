package com.sergiomartinrubio.employeeshierarchyservice.service

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.sergiomartinrubio.employeeshierarchyservice.model.EmployeeNode
import com.sergiomartinrubio.employeeshierarchyservice.utils.EmployeeNodeUtils
import com.sergiomartinrubio.employeeshierarchyservice.utils.JsonUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class EmployeesHierarchyServiceTest {

    @Mock
    private lateinit var jsonUtils: JsonUtils

    @Mock
    private lateinit var employeeNodeUtils: EmployeeNodeUtils

    @Mock
    private lateinit var employeePersistentService: EmployeePersistentService

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
        val rootEmployeeWithHierarchy = createResultEmployee()
        given(jsonUtils.transformFromJsonStringToJsonNode(jsonInput)).willReturn(firstNode)
        given(employeeNodeUtils.findRootEmployeeNode(employeesBySupervisorMap)).willReturn(rootEmployee)
        given(employeeNodeUtils.buildHierarchyTreeFromRootEmployeeNode(rootEmployee, employeesBySupervisorMap))
                .willReturn(rootEmployeeWithHierarchy)
        given(jsonUtils.transformFromRootEmployeeToJsonString(rootEmployeeWithHierarchy)).willReturn(jsonOutput)

        // WHEN
        val jsonResult = employeesHierarchyService.processHierarchy(jsonInput)

        // THEN
        assertThat(jsonResult).isEqualTo(jsonOutput)
        then(jsonUtils).should().transformFromJsonStringToJsonNode(jsonInput)
        then(employeeNodeUtils).should().findRootEmployeeNode(employeesBySupervisorMap)
        then(employeeNodeUtils).should().buildHierarchyTreeFromRootEmployeeNode(rootEmployee, employeesBySupervisorMap)
        then(employeePersistentService).should().saveEmployees(rootEmployeeWithHierarchy)
        then(jsonUtils).should().transformFromRootEmployeeToJsonString(rootEmployeeWithHierarchy)
    }

    private fun createResultEmployee(): EmployeeNode {
        return EmployeeNode(null, "Jonas",
                listOf(EmployeeNode(null, "Sophie",
                        listOf(EmployeeNode(null, "Nick",
                                listOf(EmployeeNode(null, "Pete", listOf()),
                                        EmployeeNode(null, "Barbara", listOf())))))))
    }

    private fun createEmployeesBySupervisorMap(): Map<String, List<String>> {
        return mapOf(
                "Nick" to listOf("Pete", "Barbara"),
                "Sophie" to listOf("Nick"),
                "Jonas" to listOf("Sophie")
        )
    }

    private fun createRootEmployee(): EmployeeNode {
        val rootEmployee = EmployeeNode(null, "Jonas", null)
        val employees = listOf(EmployeeNode(rootEmployee, "Sophie", listOf()))
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