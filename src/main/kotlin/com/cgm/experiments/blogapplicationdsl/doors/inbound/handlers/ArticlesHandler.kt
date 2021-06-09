package com.cgm.experiments.blogapplicationdsl.doors.inbound.handlers

import com.cgm.experiments.blogapplicationdsl.domain.model.Article
import com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories.Repository
import com.cgm.experiments.blogapplicationdsl.logger
import com.cgm.experiments.blogapplicationdsl.utilities.*
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import java.lang.Exception
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
        .let(::toJsonApiArticlesTemplate)
        .run(::okResponse)
        .apply { logger.info("Get all articles") }


    private fun findOne(id: String) =
        validateIntId(id)
            ?.let { intId -> getOneOrNotFound(intId) }
            ?: throwAndLogBadRequestException("Article id: $id not valid", "err_brGetArt")

    private fun findOneToDelete(id: String) =
        validateIntId(id)
            ?.let { intId -> deleteOneOrNotFound(intId) }
            ?: throwAndLogBadRequestException("Article not deleted because the id: $id is not valid", "err_brDelArt")

    private fun findOneToModify(id: String, article: Article) =
        validateIntId(id)
            ?.let { intId -> modifyOneOrNotFound(intId, article)}
            ?: throwAndLogBadRequestException("Article not modified because the id: $id is not valid", "err_brPutArt")

    private fun getOneOrNotFound(intId: Int) =
        repository.getOne(intId)
            ?.let(::toJsonApiArticleTemplate)
            ?.run(::okResponse)
            ?.apply { logger.info("Get article id: $intId") }
            ?: throwAndLogNotFoundException("Article id: $intId not found", "err_nfGetArt")

    private fun deleteOneOrNotFound(intId: Int) =
        repository.deleteOne(intId)
            ?.run(::okResponse)
            ?: throwAndLogNotFoundException("Article id: $intId not deleted because not found", "err_nfDelArt")

    private fun modifyOneOrNotFound(intId: Int, article: Article) =
        repository.modify(intId, article)
            ?.run(::okResponse)
            ?: throwAndLogNotFoundException("Article id: $intId not modified because not found", "err_nfPutArt")

}
