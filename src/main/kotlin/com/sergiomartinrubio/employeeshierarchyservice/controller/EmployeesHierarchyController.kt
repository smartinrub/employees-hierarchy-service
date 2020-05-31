package com.sergiomartinrubio.employeeshierarchyservice.controller

import com.sergiomartinrubio.employeeshierarchyservice.exception.InvalidInputException
import com.sergiomartinrubio.employeeshierarchyservice.service.EmployeesHierarchyService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.WebRequest

@RestController
class EmployeesHierarchyController(private val employeesHierarchyService: EmployeesHierarchyService) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/employees")
    fun buildHierarchy(@RequestBody input: String): String {
        return employeesHierarchyService.processHierarchy(input)
    }

    @ExceptionHandler(value = [(InvalidInputException::class)])
    fun handleInvalidInputException(ex: InvalidInputException, request: WebRequest): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(ex.message)
    }

}