package com.sergiomartinrubio.employeeshierarchyservice.service

import com.sergiomartinrubio.employeeshierarchyservice.model.Employee
import com.sergiomartinrubio.employeeshierarchyservice.utils.EmployeeNodeUtils
import com.sergiomartinrubio.employeeshierarchyservice.utils.JsonUtils
import org.springframework.stereotype.Service

@Service
class EmployeesHierarchyService(private val jsonUtils: JsonUtils,
                                private val employeeNodeUtils: EmployeeNodeUtils,
                                private val employeePersistentService: EmployeePersistentService) {

    fun processHierarchy(input: String): String {
        val fieldsIterator = jsonUtils.transformFromJsonStringToJsonNode(input).fields()
        val employeesBySupervisorMap = fieldsIterator.asSequence()
                .toList()
                .groupBy({ it.value.asText() }, { it.key })

        val rootEmployee = employeeNodeUtils.findRootEmployeeNode(employeesBySupervisorMap)
        val rootEmployeeWithHierarchy = employeeNodeUtils
                .buildHierarchyTreeFromRootEmployeeNode(rootEmployee, employeesBySupervisorMap)

        employeePersistentService.saveEmployees(rootEmployeeWithHierarchy)

        return jsonUtils.transformFromRootEmployeeToJsonString(rootEmployeeWithHierarchy)
    }

    fun getSupervisor(name: String): Employee {
        return employeePersistentService.getSupervisor(name)
    }
}