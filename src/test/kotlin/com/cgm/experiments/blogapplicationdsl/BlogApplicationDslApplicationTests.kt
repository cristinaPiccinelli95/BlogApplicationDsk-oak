package com.cgm.experiments.blogapplicationdsl

import com.cgm.experiments.blogapplicationdsl.domain.model.Article
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.*
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BlogApplicationDslApplicationTests {

    private lateinit var app: ConfigurableApplicationContext
    private lateinit var client: MockMvc
    private val mapper = jacksonObjectMapper()

    private val expectedArticles = listOf(
        Article(1,"article x", "body article x"),
        Article(2,"article y", "body article y"))

    @BeforeAll
    internal fun setUp() {
        app = start()
        client = MockMvcBuilders
            .webAppContextSetup(app as WebApplicationContext)
            .build()
    }

    @AfterAll
    internal fun tearDown() {
        app.close()
    }

    @BeforeEach
    internal fun before(){
        client.delete("/api/articles")
        expectedArticles.forEach {
            client.post("/api/articles"){
                contentType = MediaType.APPLICATION_JSON
                accept = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(it)
            }
        }
    }

    @Test
    fun `can read all articles`() {
        client.get("/api/articles")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content { json(mapper.writeValueAsString(expectedArticles)) }
            }
    }

    @Test
    fun `can read one article`() {
        val id = 2
        val expectedArticle = expectedArticles.first { it.id == id }

        client.get("/api/articles/$id")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content { json(mapper.writeValueAsString(expectedArticle)) }
            }
    }

    @Test
    fun `if the article do not exist return not found`() {
        client.get("/api/articles/9999999")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `if the article id is not a number it returns bad request`() {
        client.get("/api/articles/badRequestId")
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `can create a new article`() {
        val newArticle = Article(0, "article z", "body of article z")

        val articleStr = client.post("/api/articles"){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(newArticle)
        }
        .andExpect {
            status { isCreated() }
        }.andReturn().response.contentAsString

        val actualArticle = mapper.readValue<Article>(articleStr)

        client.get("/api/articles/${actualArticle.id}")
            .andExpect {
                status { isOk() }
                content { json(mapper.writeValueAsString(actualArticle)) }
            }

    }

    @Test
    fun `can delete all article`() {
        val expectedArticle = listOf<Article>()

        client.delete("/api/articles")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content { json(mapper.writeValueAsString(expectedArticle)) }
            }
    }

    @Test
    fun `can delete one article`() {
        val id = 1
        val expectedArticle = listOf(expectedArticles[1])

        client.delete("/api/articles/$id")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content { json(mapper.writeValueAsString(expectedArticle)) }
            }
    }

    @Test
    fun `if the article to delete do not exist return not found`() {
        client.delete("/api/articles/9999999")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `if the article id to delete is not a number it returns bad request`() {
        client.delete("/api/articles/badRequestId")
            .andExpect {
                status { isBadRequest() }
            }
    }

}

