package com.cgm.experiments.blogapplicationdsl.domain.model

data class TemplateAPI(val data: List<Data>)

data class Data(
    val type: String,
    val id: Int,
    val attributes: Article,
    val relationships: List<Relation>? = null,
    val included: List<Included>? = null)

data class Included(val type: String, val id: Int, val attributes: String)

data class Relation(val description: String, val data: List<DataRelation>)

data class DataRelation(val type: String, val id: Int)
