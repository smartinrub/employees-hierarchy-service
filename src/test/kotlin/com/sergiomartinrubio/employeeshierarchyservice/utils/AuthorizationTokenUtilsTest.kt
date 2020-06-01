package com.sergiomartinrubio.employeeshierarchyservice.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AuthorizationTokenUtilsTest {

    @Test
    fun test() {
        // GIVEN
        val authorizationTokenUtils = AuthorizationTokenUtils()

        // WHEN
        val tokenResult = authorizationTokenUtils.issueToken()

        // THEN
        assertThat(tokenResult).isNotNull()
    }
}