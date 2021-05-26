package com.cgm.experiments.blogapplicationdsl

import com.cgm.experiments.blogapplicationdsl.domain.model.Article
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.*
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BlogApplicationDslApplicationTests {

    private lateinit var app: ConfigurableApplicationContext
    private lateinit var client: MockMvc
    private val mapper = jacksonObjectMapper()

    private val expected = listOf(
        Article(1,"article x", "body article x"),
        Article(2,"article y", "body article y"),
        Article(3,"article z", "body article z"))

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
        expected.forEach {
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
                content { json(mapper.writeValueAsString(expected)) }
            }
    }

    @Test
    fun `can read one article`() {
        val id = 2
        val expectedArticle = expected.first { it.id == id }

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
        val newArticle = Article(0, "article a", "body of article a")

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
        val id = 2
        val expectedArticle = expected.map { it.copy() }.toMutableList()
        expectedArticle.removeIf { it.id == id }

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

    @Test
    fun `can modify one article`() {
        val id = 3
        val expectedArticle = expected.map { it.copy() }.toMutableList()
        val modifiedArticle = expected.find { it.id == id }?.copy(body = "modify body z")!!
        expectedArticle[2] = modifiedArticle

        client.put("/api/articles/$id"){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(modifiedArticle)
        }
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content { json(mapper.writeValueAsString(expectedArticle)) }
            }
    }

    @Test
    fun `if the article to modify do not exist return not found`() {
        val id = 9999999
        val modifiedArticle = Article(id, "modified title", "modified body")

        client.put("/api/articles/$id")
        {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(modifiedArticle)
        }
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `if the article id to modify is not a number it returns bad request`() {
        val modifiedArticle = Article(0, "modified title", "modified body")

        client.put("/api/articles/badRequestId")
        {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(modifiedArticle)
        }
            .andExpect {
                status { isBadRequest() }
            }
    }

}

