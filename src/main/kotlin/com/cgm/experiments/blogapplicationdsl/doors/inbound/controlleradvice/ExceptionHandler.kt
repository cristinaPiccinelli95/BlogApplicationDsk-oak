package com.cgm.experiments.blogapplicationdsl.doors.inbound.controlleradvice

import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.context.support.BeanDefinitionDsl
import org.springframework.http.HttpStatus
import org.springframework.web.context.request.WebRequest
import org.springframework.web.server.ResponseStatusException

class ResourceNotFoundException(errorMsg: String) : ResponseStatusException(HttpStatus.NOT_FOUND, errorMsg)

class ResourceBadRequestException(errorMsg: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, errorMsg)


fun BeanDefinitionDsl.enableErrorHandling () {
    bean {
        object : DefaultErrorAttributes() {
            override fun getErrorAttributes(
                webRequest: WebRequest?,
                options: ErrorAttributeOptions?
            ): MutableMap<String, Any> {
                val errorAttributes = super.getErrorAttributes(webRequest, options)

                val errorFields = composeError(errorAttributes)
                val error = Error(errorFields.first, errorFields.second, errorFields.third)

                val errorBody = mutableMapOf<String, Any>()
                errorBody["errors"] = listOf(error)
                return errorBody
            }

            private fun composeError(errorAttributes: MutableMap<String, Any>): Triple<String, String, String> {
                lateinit var message: String
                lateinit var code: String

                val status = errorAttributes["status"].toString()
                val errorMsg = errorAttributes["message"].toString()
                when {
                    errorMsg == "No message available" && errorAttributes["status"] == 404 -> {
                        message = "API Url does not exist"
                        code = "err_apiNf"
                    }
                    else -> {
                        message = errorMsg.substringBefore('-')
                        code = errorMsg.substringAfter('-')
                    }
                }
                return Triple(status, message, code)
            }
        }
    }
}

data class Error(val status: String, val message: String, val code: String)

