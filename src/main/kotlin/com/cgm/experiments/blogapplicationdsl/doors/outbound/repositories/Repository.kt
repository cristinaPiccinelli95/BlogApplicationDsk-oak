package com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories

interface Repository<T> {
    fun getAll(): List<T>
    fun getOne(id: Int): T?
    fun save(item: T): T
    fun deleteAll(): List<T>
    fun deleteOne(id: Int): MutableList<T>?
    fun modify(id: Int, item: T): MutableList<T>?
}
