package com.cgm.experiments.blogapplicationdsl

import com.cgm.experiments.blogapplicationdsl.doors.inbound.handlers.ArticlesHandler
import com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories.InMemoryArticlesRepository
import com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories.exposed.ExposedArticlesRepository
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import liquibase.integration.spring.SpringLiquibase
import org.jetbrains.exposed.sql.Database
import org.springframework.context.support.BeanDefinitionDsl
import org.springframework.context.support.beans
import org.springframework.core.env.get
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.router
import org.testcontainers.containers.PostgreSQLContainer
import javax.sql.DataSource

fun initializeContext() = beans {
    articleRoutes()
    connectToH2FromEnv()

    env["blogapplicationdsl.liquibase.change-log"]
        ?.run(::enableLiquibase)
        ?: println("Property spring.liquibase.change-log is mandatory") //andrebbe gestito con log errori
}

fun BeanDefinitionDsl.articleRoutes() {
    bean {
        router {
            "api".nest {
                val handler = ArticlesHandler(ref())

                GET("/articles", handler::find)
                GET("/articles/{id}", handler::find)
                accept(MediaType.APPLICATION_JSON).nest {
                    POST("/articles", handler::save)
                    PUT("/articles/{id}", handler::modify)
                }
                DELETE("/articles", handler::delete)
                DELETE("/articles/{id}", handler::delete)

            }
        }
    }
}

fun BeanDefinitionDsl.useRepository() {
    bean{ InMemoryArticlesRepository() }
}

fun BeanDefinitionDsl.connectToDb(connectionString: String, driver: String, username: String?, password: String?) {
    val config = HikariConfig().apply {
        jdbcUrl = connectionString
        driverClassName = driver
        maximumPoolSize = 10
        username?.let { this.username = username }
        password?.let { this.password = password }
    }
    val datasource = HikariDataSource(config)
    Database.connect(datasource)

    bean { ExposedArticlesRepository() }
    bean<DataSource> { datasource }
}

fun BeanDefinitionDsl.connectToH2FromEnv() {
    connectToDb(
        env["blogapplicationdsl.connectionstring.h2"]!!,
        env["blogapplicationdsl.driver.h2"]!!,
        env["blogapplicationdsl.username.h2"],
        env["blogapplicationdsl.password.h2"]
    )
}

fun BeanDefinitionDsl.connectToPostgreFromEnv() {
    connectToDb(
        env["blogapplicationdsl.connectionstring.postgre"]!!,
        env["blogapplicationdsl.driver.postgre"]!!,
        env["blogapplicationdsl.username.postgre"],
        env["blogapplicationdsl.password.postgre"]
    )
}

fun BeanDefinitionDsl.enableLiquibase(changeLogFile: String) {
    bean {
        SpringLiquibase().apply {
            changeLog = changeLogFile
            dataSource = ref()
        }
    }
}