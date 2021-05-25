package com.cgm.experiments.blogapplicationdsl

import com.cgm.experiments.blogapplicationdsl.doors.inbound.handlers.ArticlesHandler
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.router

@SpringBootApplication
class BlogApplicationDslApplication

fun main(args: Array<String>) {
    start(args)
}

fun start(args: Array<String> = emptyArray()) =
    runApplication<BlogApplicationDslApplication>(*args){
        addInitializers(beans {
            bean {
                router {
                    "api".nest {
                        GET("/articles", ArticlesHandler::find)
                        GET("/articles/{id}", ArticlesHandler::find)
                        accept(MediaType.APPLICATION_JSON).nest {
                            POST("/articles", ArticlesHandler::save)
                        }
                        DELETE("/articles", ArticlesHandler::delete)
                        DELETE("/articles/{id}", ArticlesHandler::delete)

                    }
                }
            }
        })
    }

