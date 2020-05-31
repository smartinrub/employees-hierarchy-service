package com.sergiomartinrubio.employeeshierarchyservice.repository

import com.sergiomartinrubio.employeeshierarchyservice.model.Employee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface EmployeeRepository : JpaRepository<Employee, String> {

    @Query(value = "SELECT * FROM employee WHERE name = (SELECT supervisor FROM employee WHERE name = ?1)",
            nativeQuery = true)
    fun getSupervisor(name: String): Employee
}