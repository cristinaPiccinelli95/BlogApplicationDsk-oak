package com.cgm.experiments.blogapplicationdsl.doors.outbound.routes.model.articles

import java.util.Objects
import com.cgm.experiments.blogapplicationdsl.doors.outbound.routes.model.articles.RelationshipComments
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
 * @param comments 
 */
data class Relationship(

    @field:Valid
    @field:JsonProperty("comments") val comments: RelationshipComments? = null
) {

}

