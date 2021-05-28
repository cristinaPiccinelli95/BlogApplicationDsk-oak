package com.cgm.experiments.blogapplicationdsl.utilities

import com.cgm.experiments.blogapplicationdsl.domain.model.Article
import com.cgm.experiments.blogapplicationdsl.doors.outbound.entities.ArticleDao

fun toArticle(articleDao: ArticleDao) =
    Article(articleDao.id.value, articleDao.title, articleDao.body)