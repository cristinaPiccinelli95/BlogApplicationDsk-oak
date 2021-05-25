package com.cgm.experiments.blogapplicationdsl.doors.inbound.handlers

import com.cgm.experiments.blogapplicationdsl.domain.model.Article
import com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories.ArticleRepository
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import java.net.URI

object ArticlesHandler{
    private val articleRepository = ArticleRepository()

    fun find(request: ServerRequest): ServerResponse =
        request.inPath("id")
            ?.run(::findOne)
            ?: okResponse(articleRepository.getAll())

    fun save(request: ServerRequest): ServerResponse =
        request.body(Article::class.java)
            .let { article -> articleRepository.save(article)}
            .let { article -> ServerResponse.created(URI("")).body(article)}

    fun delete(request: ServerRequest): ServerResponse  =
        request.inPath("id")
            ?.run(::findOneToDelete)
            ?: okResponse(articleRepository.deleteAll())

    private fun findOne(id: String) =
        validateIntId(id)
            ?.let { intId -> getOneOrNotFound(intId) }
            ?: ServerResponse.badRequest().build()

    private fun findOneToDelete(id: String) =
        validateIntId(id)
            ?.let { intId -> deleteOneOrNotFound(intId) }
            ?: ServerResponse.badRequest().build()

    private fun validateIntId(id: String) = id.toIntOrNull()

    private fun getOneOrNotFound(intId: Int) =
        articleRepository.getOne(intId)
            ?.run(::okResponse)
            ?: ServerResponse.notFound().build()

    private fun deleteOneOrNotFound(intId: Int) =
        articleRepository.deleteOne(intId)
            ?.run(::okResponse)
            ?: ServerResponse.notFound().build()

    private fun okResponse(any: Any): ServerResponse =
        ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(any)

    private fun ServerRequest.inPath(name: String): String? =
        try {
            pathVariable(name)
        }catch (ex: IllegalArgumentException){
            null
        }
}