package com.cgm.experiments.blogapplicationdsl

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.BeanDefinitionDsl

@SpringBootApplication
class BlogApplicationDslApplication

fun main(args: Array<String>) {
    start(args)
}

fun start(args: Array<String> = emptyArray(), initializer: (() -> BeanDefinitionDsl)? = null) =
    runApplication<BlogApplicationDslApplication>(*args){
        addInitializers(initializer
            ?.run { initializer() }
            ?: initializeContext())
    }

