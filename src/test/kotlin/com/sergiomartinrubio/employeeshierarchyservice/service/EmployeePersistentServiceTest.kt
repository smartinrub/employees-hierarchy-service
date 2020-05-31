package com.sergiomartinrubio.employeeshierarchyservice.service

import com.sergiomartinrubio.employeeshierarchyservice.exception.EmployeeNotFoundException
import com.sergiomartinrubio.employeeshierarchyservice.model.Employee
import com.sergiomartinrubio.employeeshierarchyservice.model.EmployeeNode
import com.sergiomartinrubio.employeeshierarchyservice.repository.EmployeeRepository
import com.sergiomartinrubio.employeeshierarchyservice.utils.EmployeeNodeUtils
import com.sergiomartinrubio.employeeshierarchyservice.utils.EmployeeTransformer
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.dao.EmptyResultDataAccessException

@ExtendWith(MockitoExtension::class)
class EmployeePersistentServiceTest {

    @Mock
    private lateinit var employeeNodeUtils: EmployeeNodeUtils

    @Mock
    private lateinit var employeeTransformer: EmployeeTransformer

    @Mock
    private lateinit var employeeRepository: EmployeeRepository

    @InjectMocks
    private lateinit var employeePersistentService: EmployeePersistentService

    @Test
    fun givenRootEmployeeWithFiveNodesWhenSaveEmployeesThenSaveIsCalledFiveTimes() {
        // GIVEN
        val rootEmployeeNode = createRootEmployee()

        val barbaraEmployeeNode = EmployeeNode(null, "Barbara", listOf())
        val peteEmployeeNode = EmployeeNode(null, "Pete", listOf())
        val nickEmployeeNode = EmployeeNode(null, "Nick", listOf(barbaraEmployeeNode, peteEmployeeNode))
        barbaraEmployeeNode.supervisor = nickEmployeeNode
        peteEmployeeNode.supervisor = nickEmployeeNode
        val sophieEmployeeNode = EmployeeNode(null, "Sophie", listOf(nickEmployeeNode))
        nickEmployeeNode.supervisor = sophieEmployeeNode
        val jonasEmployeeNode = EmployeeNode(null, "Jonas", listOf(sophieEmployeeNode))
        val employeeNodes = listOf(jonasEmployeeNode, sophieEmployeeNode, nickEmployeeNode, peteEmployeeNode, barbaraEmployeeNode)

        val barbaraEmployee = Employee("Barbara", "Nick")
        val peteEmployee = Employee("Pete", "Nick")
        val nickEmployee = Employee("Nick", "Sophie")
        val sophieEmployee = Employee("Sophie", "Jonas")
        val jonasEmployee = Employee("Jonas", null)
        given(employeeNodeUtils.getListOfEmployeeNodes(rootEmployeeNode)).willReturn(employeeNodes)
        given(employeeTransformer.transformFromEmployeeNodeToEmployee(barbaraEmployeeNode)).willReturn(barbaraEmployee)
        given(employeeTransformer.transformFromEmployeeNodeToEmployee(peteEmployeeNode)).willReturn(peteEmployee)
        given(employeeTransformer.transformFromEmployeeNodeToEmployee(nickEmployeeNode)).willReturn(nickEmployee)
        given(employeeTransformer.transformFromEmployeeNodeToEmployee(sophieEmployeeNode)).willReturn(sophieEmployee)
        given(employeeTransformer.transformFromEmployeeNodeToEmployee(jonasEmployeeNode)).willReturn(jonasEmployee)

        // WHEN
        employeePersistentService.saveEmployees(rootEmployeeNode)

        // THEN
        then(employeeRepository).should().save(barbaraEmployee)
        then(employeeRepository).should().save(peteEmployee)
        then(employeeRepository).should().save(nickEmployee)
        then(employeeRepository).should().save(sophieEmployee)
        then(employeeRepository).should().save(jonasEmployee)
        then(employeeRepository).shouldHaveNoMoreInteractions()
    }

    @Test
    fun givenEmployeeNameWhenGetSupervisorThenReturnEmployee() {
        // GIVEN
        val employeeName = "Nick"
        val supervisor = Employee("Sophie", "Jonas")
        given(employeeRepository.getSupervisor(employeeName)).willReturn(supervisor)

        // WHEN
        val supervisorResult = employeePersistentService.getSupervisor(employeeName)

        // THEN
        assertThat(supervisorResult.name).isEqualTo("Sophie")
        assertThat(supervisorResult.supervisor).isEqualTo("Jonas")
    }

    @Test
    fun givenNotFoundEmployeeNameWhenGetSupervisorThenThrowEmployeeNotFoundException() {
        // GIVEN
        val employeeName = "Nick"
        given(employeeRepository.getSupervisor(employeeName))
                .willThrow(EmptyResultDataAccessException::class.java)

        // WHEN
        // THEN
        assertThatThrownBy { employeePersistentService.getSupervisor(employeeName) }
                .isInstanceOf(EmployeeNotFoundException::class.java)
                .hasMessageContaining("Employee Nick not found!")
    }

    private fun createRootEmployee(): EmployeeNode {
        return EmployeeNode(null, "Jonas",
                listOf(EmployeeNode(null, "Sophie",
                        listOf(EmployeeNode(null, "Nick",
                                listOf(EmployeeNode(null, "Pete", listOf()),
                                        EmployeeNode(null, "Barbara", listOf())))))))
    }
}