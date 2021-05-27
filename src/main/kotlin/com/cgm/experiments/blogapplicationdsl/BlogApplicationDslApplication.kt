package com.cgm.experiments.blogapplicationdsl

import com.cgm.experiments.blogapplicationdsl.utilities.ServerPort
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.BeanDefinitionDsl

@SpringBootApplication
class BlogApplicationDslApplication

fun main(args: Array<String>) {
    start(ServerPort(8080), args)
}

fun start(port: ServerPort, args: Array<String> = emptyArray(), initializer: (() -> BeanDefinitionDsl)? = null) =
    runApplication<BlogApplicationDslApplication>(*args){
        setDefaultProperties(mapOf("server.port" to port.getPort()))
        addInitializers(initializer
            ?.run { initializer() }
            ?: initializeContext())
    }

