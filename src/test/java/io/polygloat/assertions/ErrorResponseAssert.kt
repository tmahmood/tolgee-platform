package io.polygloat.assertions

import com.fasterxml.jackson.core.JsonProcessingException
import io.polygloat.assertions.Assertions.assertThat
import io.polygloat.fixtures.parseResponseTo
import org.assertj.core.api.AbstractAssert
import org.springframework.test.web.servlet.MvcResult
import java.io.UnsupportedEncodingException

class ErrorResponseAssert(mvcResult: MvcResult?) : AbstractAssert<ErrorResponseAssert?, MvcResult?>(mvcResult, ErrorResponseAssert::class.java) {
    val isStandardValidation: StandardValidationMessageAssert
        get() {
            val standardValidation = standardMap["STANDARD_VALIDATION"]
            if (standardValidation == null) {
                failWithMessage("Error response is not standard validation type.")
            }
            return StandardValidationMessageAssert(standardValidation)
        }
    val isCustomValidation: CustomValidationMessageAssert
        get() {
            val customValidation = customMap["CUSTOM_VALIDATION"]
            if (customValidation == null) {
                failWithMessage("Error response is not custom validation type.")
            }
            return CustomValidationMessageAssert(customValidation)
        }

    fun hasCode(code: String) {
        val content: Map<String, Any> = actual!!.parseResponseTo()
        assertThat(content["code"]).isEqualTo(code)
    }

    private val standardMap: Map<String, Map<String, String>>
        get() {
            return try {
                actual!!.parseResponseTo()
            } catch (e: JsonProcessingException) {
                throw RuntimeException("Can not parse error response.")
            } catch (e: UnsupportedEncodingException) {
                throw RuntimeException("Can not parse error response.")
            }
        }
    private val customMap: Map<String, Map<String, List<Any>>>
        get() {
            return try {
                actual!!.parseResponseTo()
            } catch (e: JsonProcessingException) {
                try {
                    throw RuntimeException("""
    Can not parse error response:
    ${actual!!.response.contentAsString}
    """.trimIndent())
                } catch (unsupportedEncodingException: UnsupportedEncodingException) {
                    throw RuntimeException(unsupportedEncodingException)
                }
            } catch (e: UnsupportedEncodingException) {
                try {
                    throw RuntimeException("""
    Can not parse error response:
    ${actual!!.response.contentAsString}
    """.trimIndent())
                } catch (unsupportedEncodingException: UnsupportedEncodingException) {
                    throw RuntimeException(unsupportedEncodingException)
                }
            }
        }
}