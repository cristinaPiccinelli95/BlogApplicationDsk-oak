package com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories

import com.cgm.experiments.blogapplicationdsl.domain.model.Article

interface Repository<T> {
    fun getAll(): List<T>
    fun getOne(id: Int): Article?
    fun save(article: T): T
    fun deleteAll(): List<T>
    fun deleteOne(id: Int): MutableList<Article>?
    fun modify(id: Int, article: T): MutableList<Article>?
}
