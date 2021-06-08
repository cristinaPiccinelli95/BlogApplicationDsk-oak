package com.cgm.experiments.blogapplicationdsl.utilities

import com.cgm.experiments.blogapplicationdsl.logger
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse

fun ServerRequest.inPath(name: String): String? =
    try {
        pathVariable(name)
    }catch (ex: IllegalArgumentException){
        null
    }

fun validateIntId(id: String) = id.toIntOrNull()
fun throwException(status: HttpStatus, message: String): Nothing =
    throw ResponseStatusException(status, message)
        .apply { logger.error(message, this) }

fun okResponse(any: Any): ServerResponse =
    ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(any)