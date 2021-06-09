package com.cgm.experiments.blogapplicationdsl.utilities

import com.cgm.experiments.blogapplicationdsl.doors.inbound.controlleradvice.ResourceBadRequestException
import com.cgm.experiments.blogapplicationdsl.doors.inbound.controlleradvice.ResourceNotFoundException
import com.cgm.experiments.blogapplicationdsl.logger
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse

fun ServerRequest.inPath(name: String): String? =
    try {
        pathVariable(name)
    }catch (ex: IllegalArgumentException){
        null
    }

fun validateIntId(id: String) = id.toIntOrNull()

fun okResponse(any: Any): ServerResponse =
    ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(any)

fun throwAndLogNotFoundException(message: String, errorCode: String): Nothing =
    throw ResourceNotFoundException("$message-$errorCode")
        .apply { logger.error(message, this) }

fun throwAndLogBadRequestException(message: String, errorCode: String): Nothing =
    throw ResourceBadRequestException("$message-$errorCode")
        .apply { logger.error(message, this) }
