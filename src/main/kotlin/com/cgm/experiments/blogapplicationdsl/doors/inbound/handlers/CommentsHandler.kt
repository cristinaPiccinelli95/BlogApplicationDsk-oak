package com.cgm.experiments.blogapplicationdsl.doors.inbound.handlers

import com.cgm.experiments.blogapplicationdsl.domain.model.Comment
import com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories.Repository
import com.cgm.experiments.blogapplicationdsl.logger
import com.cgm.experiments.blogapplicationdsl.utilities.*
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import java.net.URI

class CommentsHandler(private val repository: Repository<Comment>) {

    fun save(request: ServerRequest): ServerResponse =
        request.body(Comment::class.java)
            .let { comment -> repository.save(comment)}
            .let { comment ->
                val uri = "${request.uri()}/${comment.id}"
                ServerResponse.created(URI(uri)).body(comment)}


    fun find(request: ServerRequest): ServerResponse =
        request.inPath("id")
            ?.run(::findOne)
            ?: getAll()


    private fun findOne(id: String) =
        validateIntId(id)
            ?.let { intId -> getOneOrNotFound(intId) }
            ?: throwAndLogBadRequestException("Comment id: $id not valid", "err_brGetComm")

    private fun getOneOrNotFound(intId: Int) =
        repository.getOne(intId)
            ?.run(::okResponse)
            ?.apply { logger.info("Get article id: $intId") }
            ?: throwAndLogNotFoundException("Article id: $intId not found", "err_nfGetComm")

    private fun getAll() = repository.getAll()
        .run(::okResponse)
        .apply { logger.info("Get all comments") }
}