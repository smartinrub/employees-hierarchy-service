package com.sergiomartinrubio.employeeshierarchyservice.utils

import com.sergiomartinrubio.employeeshierarchyservice.exception.InvalidEmployeeException
import com.sergiomartinrubio.employeeshierarchyservice.exception.InvalidInputException
import com.sergiomartinrubio.employeeshierarchyservice.model.EmployeeNode
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EmployeeNodeUtilsTest {

    @Test
    fun givenEmployeesBySupervisorMapWhenFindRootNodeEmployeeThenReturnRootEmployee() {
        // GIVEN
        val employeeNodeUtils = EmployeeNodeUtils()
        val employeesBySupervisorMap = createEmployeesBySupervisorMap()

        // WHEN
        val rootEmployeeResult = employeeNodeUtils.findRootEmployeeNode(employeesBySupervisorMap)

        // THEN
        assertThat(rootEmployeeResult.name).isEqualTo("Jonas")
        assertThat(rootEmployeeResult.employees!!.first().name).isEqualTo("Sophie")
        assertThat(rootEmployeeResult.supervisor).isNull()
        assertThat(rootEmployeeResult.employees!!.first().supervisor!!.name).isEqualTo("Jonas")
    }

    @Test
    fun givenEmptyMapWhenFindRootNodeEmployeeThenReturnInvalidInputException() {
        // GIVEN
        val employeeNodeUtils = EmployeeNodeUtils()
        val emptyMap = mapOf<String, List<String>>()

        // WHEN
        // THEN
        Assertions.assertThatThrownBy { employeeNodeUtils.findRootEmployeeNode(emptyMap) }
                .isInstanceOf(InvalidInputException::class.java)
                .hasMessageContaining("Please provide at least one employee/supervisor entry!")
    }

    @Test
    fun givenEmployeesBySupervisorMapWithLoopsWhenFindRootNodeEmployeeThenReturnInvalidInputException() {
        // GIVEN
        val employeeNodeUtils = EmployeeNodeUtils()
        val emptyMap = createEmployeesBySupervisorMapWithLoop()

        // WHEN
        // THEN
        Assertions.assertThatThrownBy { employeeNodeUtils.findRootEmployeeNode(emptyMap) }
                .isInstanceOf(InvalidInputException::class.java)
                .hasMessageContaining("The hierarchy contains loops!")
    }

    @Test
    fun givenRootEmployeeAndMapOfEmployeeBySupervisorWhenBuildHierarchyTreeFromRootNodeEmployeeThenReturnRootEmployWithHierarchy() {
        // GIVEN
        val employeeNodeUtils = EmployeeNodeUtils()
        val rootEmployee = createRootEmployee()
        val employeesBySupervisorMap = createEmployeesBySupervisorMap()

        // WHEN
        val employeeResult = employeeNodeUtils.buildHierarchyTreeFromRootEmployeeNode(rootEmployee, employeesBySupervisorMap)

        // THEN
        assertThat(employeeResult.name).isEqualTo("Jonas")
        assertThat(employeeResult.employees?.get(0)?.name).isEqualTo("Sophie")
        assertThat(employeeResult.employees?.get(0)?.employees?.get(0)?.name).isEqualTo("Nick")
        assertThat(employeeResult.employees?.get(0)?.employees?.get(0)?.employees?.get(0)?.name).isEqualTo("Pete")
        assertThat(employeeResult.employees?.get(0)?.employees?.get(0)?.employees?.get(1)?.name).isEqualTo("Barbara")
    }

    @Test
    fun givenInvalidEmployeeWhenBuildHierarchyTreeFromRootNodeEmployeeThenThrowInvalidEmployeeException() {
        // GIVEN
        val employeeNodeUtils = EmployeeNodeUtils()
        val invalidEmployee = EmployeeNode(null, "Jonas", null)
        val employeesBySupervisorMap = createEmployeesBySupervisorMap()

        // WHEN
        // THEN
        Assertions.assertThatThrownBy { employeeNodeUtils.buildHierarchyTreeFromRootEmployeeNode(invalidEmployee, employeesBySupervisorMap) }
                .isInstanceOf(InvalidEmployeeException::class.java)
                .hasMessageContaining("List of Employees null")
    }

    @Test
    fun givenMultipleRootsWhenBuildHierarchyTreeFromRootNodeEmployeeThenThrowInvalidInputException() {
        // GIVEN
        val employeeNodeUtils = EmployeeNodeUtils()
        val rootEmployee = createRootEmployee()
        val invalidMap = createEmployeesBySupervisorMapWithMultipleRoots()

        // WHEN
        // THEN
        Assertions.assertThatThrownBy { employeeNodeUtils.buildHierarchyTreeFromRootEmployeeNode(rootEmployee, invalidMap) }
                .isInstanceOf(InvalidInputException::class.java)
                .hasMessageContaining("Multiple roots")
    }

    @Test
    fun givenRootEmployeeNodeWhenGetListOfEmployeeNodesThenReturnListOfEmployeeNodes() {
        // GIVEN
        val employeeNodeUtils = EmployeeNodeUtils()
        val barbaraEmployeeNode = EmployeeNode(null, "Barbara", listOf())
        val peteEmployeeNode = EmployeeNode(null, "Pete", listOf())
        val nickEmployeeNode = EmployeeNode(null, "Nick", listOf(barbaraEmployeeNode, peteEmployeeNode))
        val sophieEmployeeNode = EmployeeNode(null, "Sophie", listOf(nickEmployeeNode))
        val jonasEmployeeNode = EmployeeNode(null, "Jonas", listOf(sophieEmployeeNode))

        // WHEN
        val employeeNodesResult = employeeNodeUtils.getListOfEmployeeNodes(jonasEmployeeNode)

        // THEN
        assertThat(employeeNodesResult).containsExactlyInAnyOrder(barbaraEmployeeNode, peteEmployeeNode,
                nickEmployeeNode, sophieEmployeeNode, jonasEmployeeNode)
    }

    private fun createEmployeesBySupervisorMap(): Map<String, List<String>> {
        return mapOf(
                "Nick" to listOf("Pete", "Barbara"),
                "Sophie" to listOf("Nick"),
                "Jonas" to listOf("Sophie")
        )
    }

    private fun createEmployeesBySupervisorMapWithLoop(): Map<String, List<String>> {
        return mapOf(
                "Nick" to listOf("Pete", "Barbara", "Jonas"),
                "Sophie" to listOf("Nick"),
                "Jonas" to listOf("Sophie")
        )
    }

    private fun createEmployeesBySupervisorMapWithMultipleRoots(): Map<String, List<String>> {
        return mapOf(
                "Nick" to listOf("Pete", "Barbara"),
                "Sophie" to listOf("Nick"),
                "Jonas" to listOf("Sophie"),
                "Luis" to listOf("Sergio")
        )
    }

    private fun createRootEmployee(): EmployeeNode {
        val rootEmployee = EmployeeNode(null, "Jonas", null)
        val employees = listOf(EmployeeNode(rootEmployee, "Sophie", listOf()))
        rootEmployee.employees = employees
        return rootEmployee
    }

}
