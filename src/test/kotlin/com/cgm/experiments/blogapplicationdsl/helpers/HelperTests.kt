package com.cgm.experiments.blogapplicationdsl.helpers

import com.cgm.experiments.blogapplicationdsl.connectToDb
import com.cgm.experiments.blogapplicationdsl.domain.model.Article
import com.cgm.experiments.blogapplicationdsl.domain.model.Comment
import com.cgm.experiments.blogapplicationdsl.logger
import org.springframework.context.support.BeanDefinitionDsl
import org.testcontainers.containers.PostgreSQLContainer

object HelperTests{
    val articles = listOf(
        Article(1,"article x", "body article x"),
        Article(2,"article y", "body article y"),
        Article(3,"article z", "body article z")
    )

    val comments = listOf(
        Comment(1,"good article", articles[1]),
        Comment(2,"very good article", articles[1]),
        Comment(3, "bad article", articles[2])
    )

    fun BeanDefinitionDsl.connectToPostgres(postgreSQLContainer: PostgreSQLContainer<Nothing>) {
        connectToDb(postgreSQLContainer.jdbcUrl, "org.postgresql.Driver", postgreSQLContainer.username, postgreSQLContainer.password)
        logger.info("Connected to Postgres")
    }
}