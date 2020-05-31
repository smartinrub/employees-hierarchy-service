package com.sergiomartinrubio.employeeshierarchyservice.service

import com.sergiomartinrubio.employeeshierarchyservice.utils.EmployeeHierarchyUtils
import com.sergiomartinrubio.employeeshierarchyservice.utils.JsonUtils
import org.springframework.stereotype.Service

@Service
class EmployeesHierarchyService(private val jsonUtils: JsonUtils,
                                private val employeeHierarchyUtils: EmployeeHierarchyUtils) {

    fun processHierarchy(input: String): String {
        val fieldsIterator = jsonUtils.transformFromJsonStringToJsonNode(input).fields()
        val employeesBySupervisorMap = fieldsIterator.asSequence()
                .toList()
                .groupBy({ it.value.asText() }, { it.key })

        val rootEmployee = employeeHierarchyUtils.findRootEmployee(employeesBySupervisorMap)
        val rootEmployeeWithHierarchy = employeeHierarchyUtils
                .buildHierarchyTreeFromRootEmployee(rootEmployee, employeesBySupervisorMap)

        return jsonUtils.transformFromRootEmployeeToJsonString(rootEmployeeWithHierarchy)
    }
}