package com.sergiomartinrubio.employeeshierarchyservice.model

data class EmployeeNode(var supervisor: EmployeeNode?, var name: String?, var employees: List<EmployeeNode>?)