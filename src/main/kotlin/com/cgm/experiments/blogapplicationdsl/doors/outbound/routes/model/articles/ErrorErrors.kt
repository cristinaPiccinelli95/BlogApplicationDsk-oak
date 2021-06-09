package com.cgm.experiments.blogapplicationdsl.doors.outbound.routes.model.articles

import java.util.Objects
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.DecimalMax
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size
import javax.validation.Valid

/**
 * 
 * @param status 
 * @param message 
 * @param code 
 */
data class ErrorErrors(

    @field:JsonProperty("status", required = true) val status: kotlin.String,

    @field:JsonProperty("message", required = true) val message: kotlin.String,

    @field:JsonProperty("code", required = true) val code: kotlin.String
) {

}

