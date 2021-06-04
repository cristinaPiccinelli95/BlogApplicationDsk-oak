package com.cgm.experiments.blogapplicationdsl.utilities

import com.cgm.experiments.blogapplicationdsl.domain.model.Article
import com.cgm.experiments.blogapplicationdsl.doors.outbound.entities.ArticleDao
import com.cgm.experiments.blogapplicationdsl.doors.outbound.routes.model.articles.*

fun toArticle(articleDao: ArticleDao) =
    Article(articleDao.id.value, articleDao.title, articleDao.body)

fun toArticleDto(article: Article): ArticleDto =
    ArticleDto(
        "articles",
        article.id,
        ArticleDtoAttributes(article.title, article.body),
        Relationship(RelationshipComments(listOf(CommentDto("comments", 1))))
    )

fun toJsonApiTemplateGetAll(articles: List<Article>) =
    ArticlesGetAll(articles.map(::toArticleDto))

fun toJsonApiTemplateGetOne(article: Article) =
    ArticlesGetOne(article.let(::toArticleDto))