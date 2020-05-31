package com.sergiomartinrubio.employeeshierarchyservice.repository

import com.sergiomartinrubio.employeeshierarchyservice.model.Employee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmployeeRepository : JpaRepository<Employee, String> {
}