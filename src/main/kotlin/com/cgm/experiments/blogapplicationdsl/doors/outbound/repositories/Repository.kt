package com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories

import com.cgm.experiments.blogapplicationdsl.domain.model.Article

interface Repository<T> {
    fun getAll(): List<T>
    fun getOne(intId: Int): Article?
    fun save(article: T): T
    fun deleteAll(): List<T>
    fun deleteOne(intId: Int): MutableList<Article>?
    fun modify(intId: Int, article: T): MutableList<Article>?
}
