package com.cgm.experiments.blogapplicationdsl.utilities

import com.cgm.experiments.blogapplicationdsl.domain.model.Article
import com.cgm.experiments.blogapplicationdsl.domain.model.Data
import com.cgm.experiments.blogapplicationdsl.domain.model.TemplateAPI
import com.cgm.experiments.blogapplicationdsl.doors.outbound.entities.ArticleDao

fun toArticle(articleDao: ArticleDao) =
    Article(articleDao.id.value, articleDao.title, articleDao.body)

fun toJsonApiTemplate(article: Article) =
    TemplateAPI(listOf(createData(article)))


fun createData(article: Article): Data =
    Data(type = "articles", id = article.id, attributes = article)

