package com.cgm.experiments.blogapplicationdsl.doors.inbound.controlleradvice

import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.context.request.WebRequest
import org.springframework.web.server.ResponseStatusException

class ResourceNotFoundException(errorMsg: String) : ResponseStatusException(HttpStatus.NOT_FOUND, errorMsg)

class ResourceBadRequestException(errorMsg: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, errorMsg)


@Component
class MyErrorResponse: DefaultErrorAttributes(){
    override fun getErrorAttributes(webRequest: WebRequest?, options: ErrorAttributeOptions?): MutableMap<String, Any> {
        val errorAttributes = super.getErrorAttributes(webRequest, options)
        val errorBody = mutableMapOf<String, Any>()
        val error = mutableMapOf<String, Any>()
        errorBody["errors"]=  listOf(error)
        error["status"] = errorAttributes["status"].toString()
        val errorMsg = errorAttributes["message"].toString()
        when {
            errorMsg == "No message available" && errorAttributes["status"] == 404 -> {
                error["message"] = "API Url does not exist"
                error["code"] = "err_apiNf"
            }
            else -> {
                error["message"] = errorMsg.substringBefore('-')
                error["code"] = errorMsg.substringAfter('-')
            }
        }
        return errorBody
    }
}

