package com.sergiomartinrubio.employeeshierarchyservice.model

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table
data class Employee(@Id val name: String, val supervisor: String)