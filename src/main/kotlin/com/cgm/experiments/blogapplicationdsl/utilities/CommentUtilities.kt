package com.cgm.experiments.blogapplicationdsl.utilities

import com.cgm.experiments.blogapplicationdsl.domain.model.Comment
import com.cgm.experiments.blogapplicationdsl.doors.outbound.entities.CommentDao
import com.cgm.experiments.blogapplicationdsl.doors.outbound.routes.model.articles.CommentDto

fun toComment(commentDao: CommentDao) =
    Comment(commentDao.id.value, commentDao.comment, commentDao.article.let(::toArticle))

fun toCommentDto(commentDao: CommentDao): CommentDto =
    CommentDto("comments", commentDao.id.value)