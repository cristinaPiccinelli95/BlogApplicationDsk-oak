package com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories.exposed

import com.cgm.experiments.blogapplicationdsl.domain.model.Article
import com.cgm.experiments.blogapplicationdsl.doors.outbound.entities.ArticleDao
import com.cgm.experiments.blogapplicationdsl.doors.outbound.entities.ArticleEntity
import com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories.Repository
import com.cgm.experiments.blogapplicationdsl.utilities.toArticle
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedArticlesRepository: Repository<Article> {

    override fun getAll(): List<Article> = transaction {
        ArticleDao.all().map(::toArticle)
    }

    override fun getOne(id: Int): Article? {
        TODO("Not yet implemented")
    }

    override fun save(article: Article): Article {
        TODO("Not yet implemented")
    }

    override fun deleteAll(): List<Article> {
        TODO("Not yet implemented")
    }

    override fun deleteOne(id: Int): MutableList<Article>? {
        TODO("Not yet implemented")
    }

    override fun modify(id: Int, article: Article): MutableList<Article>? {
        TODO("Not yet implemented")
    }

    fun reset() = transaction {
        ArticleEntity.deleteAll()
    }
}