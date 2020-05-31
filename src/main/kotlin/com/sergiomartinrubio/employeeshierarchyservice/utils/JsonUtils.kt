package com.sergiomartinrubio.employeeshierarchyservice.utils

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.sergiomartinrubio.employeeshierarchyservice.exception.InvalidEmployeeException
import com.sergiomartinrubio.employeeshierarchyservice.exception.InvalidInputException
import com.sergiomartinrubio.employeeshierarchyservice.model.EmployeeDto
import org.apache.logging.log4j.kotlin.logger
import org.springframework.stereotype.Component

@Component
class JsonUtils {

    private val log = logger()

    fun transformFromJsonStringToJsonNode(input: String): JsonNode {
        val mapper = ObjectMapper(JsonFactory())
        try {
            return mapper.readTree(input)
        } catch (e: JsonParseException) {
            log.info { "Invalid Input: JSON is invalid: ${e.message}" }
            throw InvalidInputException("Invalid Input: JSON is invalid")
        }
    }

    fun transformFromRootEmployeeToJsonString(rootEmployee: EmployeeDto): String {
        if (rootEmployee.name == null || rootEmployee.employees == null) {
            throw InvalidEmployeeException("Employee has name nul or list of employees null")
        }

        val mapper = ObjectMapper(JsonFactory())
        val firstNode = mapper.createObjectNode()

        buildJsonHierarchy(rootEmployee, firstNode)

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(firstNode)
    }

    private fun buildJsonHierarchy(supervisor: EmployeeDto, node: ObjectNode) {
        val newNode = node.putObject(supervisor.name)

        for (employee in supervisor.employees!!) {
            buildJsonHierarchy(employee, newNode)
        }
    }

}