package com.sergiomartinrubio.employeeshierarchyservice.filter

import com.sergiomartinrubio.employeeshierarchyservice.service.SessionService
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Component
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthenticationFilter(private val sessionService: SessionService) : Filter {


    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val request = request as HttpServletRequest
        val response = response as HttpServletResponse

        if (request.requestURI != "/authenticate") {
            val token = request.getHeader(AUTHORIZATION)

            if (token == null) {
                response.status = HttpServletResponse.SC_BAD_REQUEST
                response.writer.write("Please provide a token!")
                return
            } else {
                val isValidToken = sessionService.isValidSession(token)
                if (!isValidToken) {
                    response.status = HttpServletResponse.SC_FORBIDDEN
                    response.writer.write("Token is expired! Token is only valid for 60 seconds")
                    return
                }
            }
        }
        chain!!.doFilter(request, response)
    }


}