package com.cgm.experiments.blogapplicationdsl.doors.outbound.routes.model.articles

import java.util.Objects
import com.cgm.experiments.blogapplicationdsl.doors.outbound.routes.model.articles.ArticleDtoAttributes
import com.cgm.experiments.blogapplicationdsl.doors.outbound.routes.model.articles.Relationship
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
 * @param type 
 * @param id 
 * @param attributes 
 * @param relationship 
 */
data class ArticleDto(

    @field:JsonProperty("type", required = true) val type: kotlin.String,

    @field:JsonProperty("id", required = true) val id: kotlin.Int,

    @field:Valid
    @field:JsonProperty("attributes") val attributes: ArticleDtoAttributes? = null,

    @field:Valid
    @field:JsonProperty("relationship") val relationship: Relationship? = null
) {

}

