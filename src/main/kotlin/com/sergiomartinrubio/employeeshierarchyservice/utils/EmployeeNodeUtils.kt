package com.sergiomartinrubio.employeeshierarchyservice.utils

import com.sergiomartinrubio.employeeshierarchyservice.exception.InvalidEmployeeException
import com.sergiomartinrubio.employeeshierarchyservice.exception.InvalidInputException
import com.sergiomartinrubio.employeeshierarchyservice.model.EmployeeNode
import org.springframework.stereotype.Component

@Component
class EmployeeNodeUtils {

    fun findRootEmployeeNode(employeesBySupervisorMap: Map<String, List<String>>): EmployeeNode {
        val firstFoundSupervisor = employeesBySupervisorMap.keys.stream().findFirst()
        if (!firstFoundSupervisor.isPresent) {
            throw InvalidInputException("Invalid Data: Please provide at least one employee/supervisor entry!")
        }
        val rootSupervisorName = searchSupervisor(employeesBySupervisorMap, firstFoundSupervisor.get(), 0)

        val rootNodeSupervisor = EmployeeNode(null, null, null)
        val employees = employeesBySupervisorMap
                .getValue(rootSupervisorName)
                .map { EmployeeNode(rootNodeSupervisor, it, listOf()) }
                .toList()
        rootNodeSupervisor.supervisor = null
        rootNodeSupervisor.name = rootSupervisorName
        rootNodeSupervisor.employees = employees
        return rootNodeSupervisor
    }

    fun buildHierarchyTreeFromRootEmployeeNode(rootEmployee: EmployeeNode, employeesBySupervisorMap: Map<String, List<String>>): EmployeeNode {
        val counter = buildTreeNode(rootEmployee, employeesBySupervisorMap, 1)
        if (counter < employeesBySupervisorMap.size) {
            throw InvalidInputException("Invalid Data: Multiple roots")
        }
        return rootEmployee
    }

    fun getListOfEmployeeNodes(rootEmployee: EmployeeNode): List<EmployeeNode> {
        val employeeNodes = mutableListOf<EmployeeNode>()
        getNodeEmployee(rootEmployee, employeeNodes)
        return employeeNodes
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

    private fun buildTreeNode(currentEmployee: EmployeeNode, employeesBySupervisorMap: Map<String, List<String>>, counter: Int): Int {
        if (currentEmployee.employees == null) {
            throw InvalidEmployeeException("List of Employees null")
        }

        for (employee in currentEmployee.employees!!) {
            val employees = employeesBySupervisorMap[employee.name]
            if (employees.isNullOrEmpty()) {
                return counter
            }
            employee.employees = employees
                    .map { EmployeeNode(employee, it, listOf()) }
                    .toList()
            return buildTreeNode(employee, employeesBySupervisorMap, counter.inc())
        }
        return counter
    }

    private fun getNodeEmployee(employeeNode: EmployeeNode, employeeNodes: MutableList<EmployeeNode>) {
        employeeNodes.add(employeeNode)
        if (employeeNode.employees == null) {
            return
        }
        for (employee in employeeNode.employees!!) {
            getNodeEmployee(employee, employeeNodes)
        }
    }

}