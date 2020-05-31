package com.sergiomartinrubio.employeeshierarchyservice.utils

import com.sergiomartinrubio.employeeshierarchyservice.exception.InvalidEmployeeException
import com.sergiomartinrubio.employeeshierarchyservice.exception.InvalidInputException
import com.sergiomartinrubio.employeeshierarchyservice.model.EmployeeDto
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EmployeeHierarchyUtilsTest {

    @Test
    fun givenEmployeesBySupervisorMapWhenFindRootEmployeeThenReturnRootEmployee() {
        // GIVEN
        val employeeHierarchyUtils = EmployeeHierarchyUtils()
        val employeesBySupervisorMap = createEmployeesBySupervisorMap()

        // WHEN
        val rootEmployeeResult = employeeHierarchyUtils.findRootEmployee(employeesBySupervisorMap)

        // THEN
        assertThat(rootEmployeeResult.name).isEqualTo("Jonas")
        assertThat(rootEmployeeResult.employees!!.first().name).isEqualTo("Sophie")
        assertThat(rootEmployeeResult.supervisor).isNull()
    }

    @Test
    fun givenEmptyMapWhenFindRootEmployeeThenReturnInvalidInputException() {
        // GIVEN
        val employeeHierarchyUtils = EmployeeHierarchyUtils()
        val emptyMap = mapOf<String, List<String>>()

        // WHEN
        // THEN
        Assertions.assertThatThrownBy { employeeHierarchyUtils.findRootEmployee(emptyMap) }
                .isInstanceOf(InvalidInputException::class.java)
                .hasMessageContaining("Please provide at least one employee/supervisor entry!")
    }

    @Test
    fun givenEmployeesBySupervisorMapWithLoopsWhenFindRootEmployeeThenReturnInvalidInputException() {
        // GIVEN
        val employeeHierarchyUtils = EmployeeHierarchyUtils()
        val emptyMap = createEmployeesBySupervisorMapWithLoop()

        // WHEN
        // THEN
        Assertions.assertThatThrownBy { employeeHierarchyUtils.findRootEmployee(emptyMap) }
                .isInstanceOf(InvalidInputException::class.java)
                .hasMessageContaining("The hierarchy contains loops!")
    }

    @Test
    fun givenRootEmployeeAndMapOfEmployeeBySupervisorWhenBuildHierarchyTreeFromRootEmployeeThenReturnRootEmployWithHierarchy() {
        // GIVEN
        val employeeHierarchyUtils = EmployeeHierarchyUtils()
        val rootEmployee = createRootEmployee()
        val employeesBySupervisorMap = createEmployeesBySupervisorMap()

        // WHEN
        val employeeResult = employeeHierarchyUtils.buildHierarchyTreeFromRootEmployee(rootEmployee, employeesBySupervisorMap)

        // THEN
        assertThat(employeeResult.name).isEqualTo("Jonas")
        assertThat(employeeResult.employees?.get(0)?.name).isEqualTo("Sophie")
        assertThat(employeeResult.employees?.get(0)?.employees?.get(0)?.name).isEqualTo("Nick")
        assertThat(employeeResult.employees?.get(0)?.employees?.get(0)?.employees?.get(0)?.name).isEqualTo("Pete")
        assertThat(employeeResult.employees?.get(0)?.employees?.get(0)?.employees?.get(1)?.name).isEqualTo("Barbara")
    }

    @Test
    fun givenInvalidEmployeeWhenBuildHierarchyTreeFromRootEmployeeThenThrowInvalidEmployeeException() {
        // GIVEN
        val employeeHierarchyUtils = EmployeeHierarchyUtils()
        val invalidEmployee = EmployeeDto(null, "Jonas", null)
        val employeesBySupervisorMap = createEmployeesBySupervisorMap()

        // WHEN
        // THEN
        Assertions.assertThatThrownBy { employeeHierarchyUtils.buildHierarchyTreeFromRootEmployee(invalidEmployee, employeesBySupervisorMap) }
                .isInstanceOf(InvalidEmployeeException::class.java)
                .hasMessageContaining("List of Employees null")
    }

    @Test
    fun givenMultipleRootsWhenBuildHierarchyTreeFromRootEmployeeThenThrowInvalidInputException() {
        // GIVEN
        val employeeHierarchyUtils = EmployeeHierarchyUtils()
        val rootEmployee = createRootEmployee()
        val invalidMap = createEmployeesBySupervisorMapWithMultipleRoots()

        // WHEN
        // THEN
        Assertions.assertThatThrownBy { employeeHierarchyUtils.buildHierarchyTreeFromRootEmployee(rootEmployee, invalidMap) }
                .isInstanceOf(InvalidInputException::class.java)
                .hasMessageContaining("Multiple roots")
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

    private fun createRootEmployee(): EmployeeDto {
        val rootEmployee = EmployeeDto(null, "Jonas", null)
        val employees = listOf(EmployeeDto(rootEmployee, "Sophie", listOf()))
        rootEmployee.employees = employees
        return rootEmployee
    }

}
