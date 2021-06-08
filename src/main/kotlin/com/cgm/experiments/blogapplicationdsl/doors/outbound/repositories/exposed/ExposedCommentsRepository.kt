package com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories.exposed

import com.cgm.experiments.blogapplicationdsl.domain.model.Comment
import com.cgm.experiments.blogapplicationdsl.doors.outbound.entities.ArticleDao
import com.cgm.experiments.blogapplicationdsl.doors.outbound.entities.ArticleEntity
import com.cgm.experiments.blogapplicationdsl.doors.outbound.entities.CommentDao
import com.cgm.experiments.blogapplicationdsl.doors.outbound.entities.CommentEntity
import com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories.Repository
import com.cgm.experiments.blogapplicationdsl.utilities.searchArticle
import com.cgm.experiments.blogapplicationdsl.utilities.toComment
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedCommentsRepository: Repository<Comment> {

    override fun getAll(): List<Comment> = transaction {
        CommentDao.all().map(::toComment)
    }

    override fun getOne(id: Int): Comment? = transaction {
        CommentDao.findById(id)
            ?.let(::toComment)
    }

    override fun save(comm: Comment): Comment = transaction {
        CommentDao.new {
            comment = comm.comment
            article = searchArticle(comm)
        }.let(::toComment)
    }

    override fun deleteAll(): List<Comment> = transaction {
        reset()
        getAll()
    }

    override fun deleteOne(id: Int): MutableList<Comment>? {
        TODO("Not yet implemented")
    }

    override fun modify(id: Int, comment: Comment): MutableList<Comment>? {
        TODO("Not yet implemented")
    }

    private fun reset() = transaction {
        CommentEntity.deleteAll()
    }
}