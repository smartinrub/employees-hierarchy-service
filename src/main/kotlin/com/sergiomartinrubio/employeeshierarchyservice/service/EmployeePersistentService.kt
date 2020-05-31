package com.sergiomartinrubio.employeeshierarchyservice.service

import com.sergiomartinrubio.employeeshierarchyservice.model.EmployeeNode
import com.sergiomartinrubio.employeeshierarchyservice.repository.EmployeeRepository
import com.sergiomartinrubio.employeeshierarchyservice.utils.EmployeeNodeUtils
import com.sergiomartinrubio.employeeshierarchyservice.utils.EmployeeTransformer
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class EmployeePersistentService(private val employeeNodeUtils: EmployeeNodeUtils,
                                private val employeeTransformer: EmployeeTransformer,
                                private val employeeRepository: EmployeeRepository) {

    @Transactional
    fun saveEmployees(rootEmployeeNode: EmployeeNode) {
        val employeeNodes = employeeNodeUtils.getListOfEmployeeNodes(rootEmployeeNode)

        for (employeeNode in employeeNodes) {
            val employee = employeeTransformer.transformFromEmployeeNodeToEmployee(employeeNode)
            employeeRepository.save(employee)
        }
    }

}