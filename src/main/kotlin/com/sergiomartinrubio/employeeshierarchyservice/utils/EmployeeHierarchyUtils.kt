package com.sergiomartinrubio.employeeshierarchyservice.utils

import com.sergiomartinrubio.employeeshierarchyservice.exception.InvalidEmployeeException
import com.sergiomartinrubio.employeeshierarchyservice.exception.InvalidInputException
import com.sergiomartinrubio.employeeshierarchyservice.model.Employee
import org.springframework.stereotype.Component

@Component
class EmployeeHierarchyUtils {

    fun findRootEmployee(employeesBySupervisorMap: Map<String, List<String>>): Employee {
        val firstFoundSupervisor = employeesBySupervisorMap.keys.stream().findFirst()
        if (!firstFoundSupervisor.isPresent) {
            throw InvalidInputException("Invalid Data: Please provide at least one employee/supervisor entry!")
        }
        val rootSupervisorName = searchSupervisor(employeesBySupervisorMap, firstFoundSupervisor.get(), 0)

        var rootSupervisor = Employee(null, null, null)
        val employees = employeesBySupervisorMap
                .getValue(rootSupervisorName)
                .map { Employee(rootSupervisor, it, listOf()) }
                .toList()
        rootSupervisor = Employee(null, rootSupervisorName, employees)
        return rootSupervisor
    }

    fun buildHierarchyTreeFromRootEmployee(rootEmployee: Employee, employeesBySupervisorMap: Map<String, List<String>>): Employee {
        val counter = buildTreeNode(rootEmployee, employeesBySupervisorMap, 1)
        if (counter < employeesBySupervisorMap.size) {
            throw InvalidInputException("Invalid Data: Multiple roots")
        }
        return rootEmployee
    }

    private fun searchSupervisor(employeesBySupervisorMap: Map<String, List<String>>,
                                 currentSupervisor: String, counter: Int): String {
        for ((supervisor, employees) in employeesBySupervisorMap) {
            if (employees.contains(currentSupervisor)) {
                if (counter > employeesBySupervisorMap.size) {
                    throw InvalidInputException("Invalid Data: The hierarchy contains loops!")
                }
                return searchSupervisor(employeesBySupervisorMap, supervisor, counter.inc())
            }
        }
        return currentSupervisor
    }

    private fun buildTreeNode(currentEmployee: Employee, employeesBySupervisorMap: Map<String, List<String>>, counter: Int): Int {
        if (currentEmployee.employees == null) {
            throw InvalidEmployeeException("List of Employees null")
        }

        for (employee in currentEmployee.employees!!) {
            val employees = employeesBySupervisorMap[employee.name]
            if (employees.isNullOrEmpty()) {
                return counter
            }
            employee.employees = employees
                    .map { Employee(employee, it, listOf()) }
                    .toList()
            return buildTreeNode(employee, employeesBySupervisorMap, counter.inc())
        }
        return counter
    }

}