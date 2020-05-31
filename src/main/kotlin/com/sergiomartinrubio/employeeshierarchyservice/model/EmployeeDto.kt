package com.sergiomartinrubio.employeeshierarchyservice.model

data class EmployeeDto(val supervisor: EmployeeDto?, val name: String?, var employees: List<EmployeeDto>?)