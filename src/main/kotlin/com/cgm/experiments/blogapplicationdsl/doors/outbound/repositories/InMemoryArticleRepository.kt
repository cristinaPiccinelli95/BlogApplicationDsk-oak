package com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories

import com.cgm.experiments.blogapplicationdsl.domain.model.Article

class InMemoryArticleRepository(initialValue: List<Article> = emptyList()) : Repository<Article> {
    private val articles = initialValue.toMutableList()

    override fun getAll(): List<Article> = articles

    override fun getOne(id: Int): Article? = articles.firstOrNull { it.id == id }

    override fun save(article: Article): Article{
        val newId = articles.maxByOrNull { it.id }?.id?.plus(1) ?: 1
        val newArticle = article.copy(id = newId)
        articles.add(newArticle)
        return newArticle
    }

    override fun deleteAll(): List<Article> {
        articles.clear()
        return articles
    }

    override fun deleteOne(id: Int): MutableList<Article>? =
        getOne(id)
            ?.let {
                articles.remove(it)
                articles
            }

    override fun modify(id: Int, article: Article): MutableList<Article>? =
        getOne(id)
            ?.let { articleToModify ->
                val index = articles.indexOf(articleToModify)
                articles[index] = article
                articles
            }

    fun reset(initialValue: List<Article> = emptyList()){
        articles.clear()
            .apply { articles
                .addAll(initialValue)}
    }

}