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
 * @param title 
 * @param body 
 */
data class ArticleDtoAttributes(

    @field:JsonProperty("title") val title: kotlin.String? = null,

    @field:JsonProperty("body") val body: kotlin.String? = null
) {

}

