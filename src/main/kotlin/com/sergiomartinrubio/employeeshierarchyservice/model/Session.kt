package com.sergiomartinrubio.employeeshierarchyservice.model

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Session(@Id val token: String, val creationDateTime: LocalDateTime)