package com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories

import com.cgm.experiments.blogapplicationdsl.domain.model.Article

class ArticleRepository(){
    private val articles = mutableListOf(
        Article(1, "article x", "body article x"),
        Article(2, "article y", "body article y")
    )

    fun getAll(): List<Article> = articles

    fun getOne(id: Int): Article? = articles.firstOrNull { it.id == id }

    fun save(article: Article): Article{
        val newId = articles.maxByOrNull { it.id }?.id?.plus(1) ?: 1
        val newArticle = article.copy(id = newId)
        articles.add(newArticle)
        return newArticle
    }

    fun deleteAll(): List<Article> {
        articles.clear()
        return articles
    }

    fun deleteOne(id: Int): MutableList<Article>? =
        articles.firstOrNull { it.id == id }
            ?.let {
                articles.remove(it)
                articles
            }

}