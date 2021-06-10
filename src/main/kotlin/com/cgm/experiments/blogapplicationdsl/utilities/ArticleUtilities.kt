package com.cgm.experiments.blogapplicationdsl.utilities

import com.cgm.experiments.blogapplicationdsl.domain.model.Article
import com.cgm.experiments.blogapplicationdsl.domain.model.Comment
import com.cgm.experiments.blogapplicationdsl.doors.outbound.entities.ArticleDao
import com.cgm.experiments.blogapplicationdsl.doors.outbound.entities.ArticleEntity
import com.cgm.experiments.blogapplicationdsl.doors.outbound.routes.model.articles.*
import org.jetbrains.exposed.sql.transactions.transaction

fun toArticle(articleDao: ArticleDao) =
    Article(articleDao.id.value, articleDao.title, articleDao.body)

fun toArticleDto(article: Article): ArticleDto =
    ArticleDto(
        "articles",
        article.id,
        ArticleDtoAttributes(article.title, article.body),
        Relationship(RelationshipComments(mapCommentsToDto(article)))
    )

private fun findComments(article: Article) = transaction {
    ArticleDao
        .find { ArticleEntity.title eq article.title }
        .first()
        .comments
}

private fun mapCommentsToDto(article: Article) = transaction {
    findComments(article)
        .map(::toCommentDto)
}

fun toJsonApiArticlesTemplate(articles: List<Article>) =
    ArticlesGetAll(articles.map(::toArticleDto))

fun toJsonApiArticleTemplate(article: Article) =
    ArticlesGetOne(article.let(::toArticleDto))

fun toJsonApiWithIncludeArticleTemplate(article: Article) =
    CommentIncluded(
        ArticlesGetOne(article.let(::toArticleDto)),
        findComments(article)
            .map(::toComment)
    )

fun searchArticle(comment: Comment) = transaction {
    ArticleDao
        .find { ArticleEntity.title eq comment.article.title }
        .first()
}

data class CommentIncluded(val articlesGetOne: ArticlesGetOne, val map: List<Comment>)

