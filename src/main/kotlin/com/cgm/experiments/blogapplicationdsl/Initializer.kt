package com.cgm.experiments.blogapplicationdsl

import com.cgm.experiments.blogapplicationdsl.doors.inbound.handlers.ArticlesHandler
import com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories.InMemoryArticlesRepository
import com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories.exposed.ExposedArticlesRepository
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import liquibase.integration.spring.SpringLiquibase
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.jetbrains.exposed.sql.Database
import org.springframework.context.support.BeanDefinitionDsl
import org.springframework.context.support.beans
import org.springframework.core.env.get
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.web.servlet.function.router
import javax.sql.DataSource

val logger: Logger = LogManager.getLogger()

fun initializeContext() = beans {
    articleRoutes()
    connectToPostgreFromEnv()

    env["blogapplicationdsl.liquibase.change-log"]
        ?.run(::enableLiquibase)
        ?: logger.error("Property spring.liquibase.change-log is mandatory")
}

fun BeanDefinitionDsl.articleRoutes() {
    bean {
        router {
            "api/oak-test/v1".nest {
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

    logger.info("Connected to H2")
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

fun BeanDefinitionDsl.enableSecurity () {
    bean {
        object : WebSecurityConfigurerAdapter(){
            override fun configure(http: HttpSecurity) {
                http
                    .authorizeRequests { authz ->
                        authz
                            .antMatchers("/api/**").hasAnyAuthority("ROLE_ADMIN")
                            .antMatchers("/public/**").permitAll()
                            .antMatchers("/**").denyAll()
                    }
                    .httpBasic()
            }
        }
    }
}