package com.sergiomartinrubio.employeeshierarchyservice.utils

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.sergiomartinrubio.employeeshierarchyservice.exception.InvalidEmployeeException
import com.sergiomartinrubio.employeeshierarchyservice.exception.InvalidInputException
import com.sergiomartinrubio.employeeshierarchyservice.model.EmployeeDto
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class JsonUtilsTest {

    @Test
    fun givenJsonStringWhenTransformFromJsonStringToJsonNodeThenReturnJsonNode() {
        // GIVEN
        val jsonString = "{\"Pete\": \"Nick\", \"Barbara\": \"Nick\", \"Nick\": \"Sophie\", \"Sophie\": \"Jonas\"}"
        val jsonUtils = JsonUtils()
        val mapper = ObjectMapper(JsonFactory())
        val firstNode = createFirstNode(mapper)

        // WHEN
        val jsonNodeResult = jsonUtils.transformFromJsonStringToJsonNode(jsonString)

        // THEN
        assertThat(jsonNodeResult).isEqualTo(firstNode)
    }

    @Test
    fun givenInvalidJsonStringWhenTransformFromJsonStringToJsonNodeThenThrowInvalidInputException() {
        // GIVEN
        val invalidJsonString = "{\"Pete\" = \"Nick\"}"
        val jsonUtils = JsonUtils()

        // WHEN
        // THEN
        Assertions.assertThatThrownBy { jsonUtils.transformFromJsonStringToJsonNode(invalidJsonString) }
                .isInstanceOf(InvalidInputException::class.java)
                .hasMessageContaining("JSON is invalid")
    }

    @Test
    fun givenRootEmployeeWhenTransformFromRootEmployeeToJsonThenJsonString() {
        // GIVEN
        val jsonUtils = JsonUtils()
        val jsonResult = "{\n" +
                "  \"Jonas\" : {\n" +
                "    \"Sophie\" : {\n" +
                "      \"Nick\" : {\n" +
                "        \"Pete\" : { },\n" +
                "        \"Barbara\" : { }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"

        // WHEN
        val jsonStringResult = jsonUtils.transformFromRootEmployeeToJsonString(createResultEmployee())

        // THEN
        assertThat(jsonStringResult).isEqualTo(jsonResult)
    }

    @Test
    fun givenNullEmployeeNameWhenTransformFromRootEmployeeToJsonThenThrowInvalidEmployeeException() {
        // GIVEN
        val jsonUtils = JsonUtils()

        // WHEN
        // THEN
        Assertions.assertThatThrownBy { jsonUtils.transformFromRootEmployeeToJsonString(EmployeeDto(null, null, null)) }
                .isInstanceOf(InvalidEmployeeException::class.java)
                .hasMessageContaining("Employee has name nul or list of employees null")
    }

    private fun createFirstNode(mapper: ObjectMapper): ObjectNode? {
        return mapper.createObjectNode()
                .put("Pete", "Nick")
                .put("Barbara", "Nick")
                .put("Nick", "Sophie")
                .put("Sophie", "Jonas")
    }

    private fun createResultEmployee(): EmployeeDto {
        return EmployeeDto(null, "Jonas",
                listOf(EmployeeDto(null, "Sophie",
                        listOf(EmployeeDto(null, "Nick",
                                listOf(EmployeeDto(null, "Pete", listOf()),
                                        EmployeeDto(null, "Barbara", listOf())))))))
    }

}