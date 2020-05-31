package com.sergiomartinrubio.employeeshierarchyservice.service

import com.sergiomartinrubio.employeeshierarchyservice.exception.EmployeeNotFoundException
import com.sergiomartinrubio.employeeshierarchyservice.model.Employee
import com.sergiomartinrubio.employeeshierarchyservice.model.EmployeeNode
import com.sergiomartinrubio.employeeshierarchyservice.repository.EmployeeRepository
import com.sergiomartinrubio.employeeshierarchyservice.utils.EmployeeNodeUtils
import com.sergiomartinrubio.employeeshierarchyservice.utils.EmployeeTransformer
import org.apache.logging.log4j.kotlin.logger
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class EmployeePersistentService(private val employeeNodeUtils: EmployeeNodeUtils,
                                private val employeeTransformer: EmployeeTransformer,
                                private val employeeRepository: EmployeeRepository) {

    private val log = logger()

    @Transactional
    fun saveEmployees(rootEmployeeNode: EmployeeNode) {
        val employeeNodes = employeeNodeUtils.getListOfEmployeeNodes(rootEmployeeNode)

        for (employeeNode in employeeNodes) {
            val employee = employeeTransformer.transformFromEmployeeNodeToEmployee(employeeNode)
            employeeRepository.save(employee)
        }
    }

    fun getSupervisor(name: String): Employee {
        try {
            return employeeRepository.getSupervisor(name)
        } catch (e: EmptyResultDataAccessException) {
            log.info { "Employee $name not found: e.message" }
            throw EmployeeNotFoundException("Employee $name not found!")
        }
    }

}