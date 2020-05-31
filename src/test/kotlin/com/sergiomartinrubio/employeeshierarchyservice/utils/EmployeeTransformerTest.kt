package com.sergiomartinrubio.employeeshierarchyservice.utils

import com.sergiomartinrubio.employeeshierarchyservice.model.EmployeeNode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EmployeeTransformerTest {

    @Test
    fun givenEmployeeNodeWhenTransformFromEmployeeNodeToEmployeeThenReturnEmployee() {
        // GIVEN
        val employeeTransformer = EmployeeTransformer()

        // WHEN
        val employeeResult = employeeTransformer
                .transformFromEmployeeNodeToEmployee(EmployeeNode(null, "Jonas", listOf()))

        // THEN
        assertThat(employeeResult.name).isEqualTo("Jonas")
        assertThat(employeeResult.supervisor).isNull()
    }
}