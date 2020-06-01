package com.sergiomartinrubio.employeeshierarchyservice.model

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Credentials(@Id val username: String, val password: String)