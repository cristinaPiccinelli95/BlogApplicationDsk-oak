package com.cgm.experiments.blogapplicationdsl.doors.outbound.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object CommentEntity: IntIdTable("blog.comments") {
    val comment: Column<String> = varchar("comment", 2000)
    val article = reference("article", ArticleEntity)
}

class CommentDao(id: EntityID<Int>): IntEntity(id){
    companion object: IntEntityClass<CommentDao>(CommentEntity)

    var comment by CommentEntity.comment
    var article by ArticleDao referencedOn CommentEntity.article
}