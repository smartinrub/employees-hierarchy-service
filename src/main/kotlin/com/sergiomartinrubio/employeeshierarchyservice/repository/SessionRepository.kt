package com.sergiomartinrubio.employeeshierarchyservice.repository

import com.sergiomartinrubio.employeeshierarchyservice.model.Session
import org.springframework.data.jpa.repository.JpaRepository

interface SessionRepository: JpaRepository<Session, String> {
}