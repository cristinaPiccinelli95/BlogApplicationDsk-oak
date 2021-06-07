package com.cgm.experiments.blogapplicationdsl.doors.inbound.handlers

import com.cgm.experiments.blogapplicationdsl.domain.model.Article
import com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories.Repository
import com.cgm.experiments.blogapplicationdsl.logger
import com.cgm.experiments.blogapplicationdsl.utilities.toJsonApiTemplateGetAll
import com.cgm.experiments.blogapplicationdsl.utilities.toJsonApiTemplateGetOne
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import java.net.URI

class ArticlesHandler(private val repository: Repository<Article>){

    fun find(request: ServerRequest): ServerResponse =
        request.inPath("id")
            ?.run(::findOne)
            ?: getAll()

    fun save(request: ServerRequest): ServerResponse =
        request.body(Article::class.java)
            .let { article -> repository.save(article)}
            .let { article ->
                val uri = "${request.uri()}/${article.id}"
                ServerResponse.created(URI(uri)).body(article)}

    fun delete(request: ServerRequest): ServerResponse =
        request.inPath("id")
            ?.run(::findOneToDelete)
            ?: okResponse(repository.deleteAll())

    fun modify(request: ServerRequest): ServerResponse  =
        findOneToModify(request.pathVariable("id"), request.body(Article::class.java))

    private fun getAll() = repository.getAll()
        .let(::toJsonApiTemplateGetAll)
        .run(::okResponse)
        .apply { logger.info("Get all articles") }


    private fun findOne(id: String) =
        validateIntId(id)
            ?.let { intId -> getOneOrNotFound(intId) }
            ?: throwException(HttpStatus.BAD_REQUEST, "Article id not valid")

    private fun findOneToDelete(id: String) =
        validateIntId(id)
            ?.let { intId -> deleteOneOrNotFound(intId) }
            ?: throwException(HttpStatus.BAD_REQUEST, "Article not deleted because the id not valid")

    private fun findOneToModify(id: String, article: Article) =
        validateIntId(id)
            ?.let { intId -> modifyOneOrNotFound(intId, article)}
            ?: throwException(HttpStatus.BAD_REQUEST, "Article not modified because the id not valid")

    private fun validateIntId(id: String) = id.toIntOrNull()

    private fun getOneOrNotFound(intId: Int) =
        repository.getOne(intId)
            ?.let(::toJsonApiTemplateGetOne)
            ?.run(::okResponse)
            ?.apply { logger.info("Get article id: $intId") }
            ?: throwException(HttpStatus.NOT_FOUND, "Article id: $intId not found")

    private fun deleteOneOrNotFound(intId: Int) =
        repository.deleteOne(intId)
            ?.run(::okResponse)
            ?: throwException(HttpStatus.NOT_FOUND, "Article id: $intId not deleted because not found")

    private fun modifyOneOrNotFound(intId: Int, article: Article) =
        repository.modify(intId, article)
            ?.run(::okResponse)
            ?: throwException(HttpStatus.NOT_FOUND, "Article id: $intId not modified because not found")

    private fun okResponse(any: Any): ServerResponse =
        ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(any)

    fun throwException(status: HttpStatus, message: String): Nothing =
        throw ResponseStatusException(status, message)
            .apply { logger.info(message) }

    private fun ServerRequest.inPath(name: String): String? =
        try {
            pathVariable(name)
        }catch (ex: IllegalArgumentException){
            null
        }

}
