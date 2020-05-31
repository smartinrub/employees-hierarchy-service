package com.sergiomartinrubio.employeeshierarchyservice.utils

import com.sergiomartinrubio.employeeshierarchyservice.model.Employee
import com.sergiomartinrubio.employeeshierarchyservice.model.EmployeeNode
import org.springframework.stereotype.Component

@Component
class EmployeeTransformer {

    fun transformFromEmployeeNodeToEmployee(employeeNode: EmployeeNode): Employee {
        val employeeName = employeeNode.name
        val employeeSupervisorName = if (employeeNode.supervisor == null) null else employeeNode.supervisor!!.name
        return Employee(employeeName, employeeSupervisorName)
    }
}