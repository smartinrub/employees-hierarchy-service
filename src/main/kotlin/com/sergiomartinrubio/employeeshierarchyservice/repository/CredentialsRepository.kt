package com.sergiomartinrubio.employeeshierarchyservice.repository

import com.sergiomartinrubio.employeeshierarchyservice.model.Credentials
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CredentialsRepository : JpaRepository<Credentials, String> {
}