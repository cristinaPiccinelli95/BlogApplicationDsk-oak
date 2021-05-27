package com.cgm.experiments.blogapplicationdsl

import com.cgm.experiments.blogapplicationdsl.doors.inbound.handlers.ArticlesHandler
import com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories.InMemoryArticleRepository
import org.springframework.context.support.BeanDefinitionDsl
import org.springframework.context.support.beans
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.router

fun initializeContext() = beans {
    useInMemoryRepository()
    articleRoutes()
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

fun BeanDefinitionDsl.useInMemoryRepository() {
    bean { InMemoryArticleRepository() }
}