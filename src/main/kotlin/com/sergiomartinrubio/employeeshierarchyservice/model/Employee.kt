package com.sergiomartinrubio.employeeshierarchyservice.model

data class Employee(val supervisor: Employee?, val name: String?, var employees: List<Employee>?)