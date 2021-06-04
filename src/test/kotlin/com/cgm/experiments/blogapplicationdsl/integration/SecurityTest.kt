package com.cgm.experiments.blogapplicationdsl.integration

import com.cgm.experiments.blogapplicationdsl.enableSecurity
import com.cgm.experiments.blogapplicationdsl.start
import com.cgm.experiments.blogapplicationdsl.utilities.ServerPort
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.support.BeanDefinitionDsl
import org.springframework.context.support.beans
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.security.core.userdetails.User
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SecurityTest {

    private lateinit var app: ConfigurableApplicationContext
    private lateinit var client: TestRestTemplate
    private val port = ServerPort(8080)

    @BeforeAll
    internal fun setUp() {
        app = start(port){
            beans {
                bean{
                    router{
                        GET("/api/test"){ ServerResponse.ok().body("test") }
                        GET("/public/test"){ ServerResponse.ok().body("test") }
                    }
                }
                enableSecurity()
                inMemoryAdmin("admin", "password")
            }
        }

        client = RestTemplateBuilder()
            .rootUri("http://localhost:${port.getPort()}")
            .run(::TestRestTemplate)
    }

    @Test
    fun `can secure endpoints under api route`(){
        val response = client.getForEntity("/api/test", String::class.java)

        response.statusCode shouldBe HttpStatus.UNAUTHORIZED
    }

    @Test
    fun `endpoint under public route are not secured`(){
        val response = client.getForEntity("/public/test", String::class.java)

        response.statusCode shouldBe HttpStatus.OK
    }

    @Test
    fun `can get from a secured endpoint`(){
        val response = RequestEntity
            .get("/api/test")
            .header("Authorization", "Basic ${("admin:password".toBase64())}")
            .build()
            .run { client.exchange<String>(this) }

        response.statusCode shouldBe HttpStatus.OK
    }

    private fun String.toBase64(): String = Base64.getEncoder().encodeToString(toByteArray())

    private fun BeanDefinitionDsl.inMemoryAdmin(user: String, password: String) {
        bean {
            User
                .withUsername(user)
                .password("{noop}$password")
                .roles("ADMIN")
                .build()
                .run { InMemoryUserDetailsManager(this) }
        }
    }
}

