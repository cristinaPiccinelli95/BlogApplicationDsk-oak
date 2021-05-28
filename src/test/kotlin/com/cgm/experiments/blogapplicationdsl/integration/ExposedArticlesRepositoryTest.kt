package com.cgm.experiments.blogapplicationdsl.integration

import com.cgm.experiments.blogapplicationdsl.*
import com.cgm.experiments.blogapplicationdsl.domain.model.Article
import com.cgm.experiments.blogapplicationdsl.doors.outbound.entities.ArticleDao
import com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories.exposed.ExposedArticlesRepository
import com.cgm.experiments.blogapplicationdsl.helpers.HelperTests
import com.cgm.experiments.blogapplicationdsl.utilities.ServerPort
import com.cgm.experiments.blogapplicationdsl.utilities.toArticle
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.support.beans

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExposedArticlesRepositoryTest {

    private lateinit var app: ConfigurableApplicationContext

    private val initialArticles = HelperTests.articles

    private val exposedArticlesRepository = ExposedArticlesRepository()

    private val changeLogFile = "classpath:/liquibase/db-changelog-master.xml"


    @BeforeAll
    internal fun setUp() {
        app = start(ServerPort()){
            beans {
                articleRoutes()
                connectToPostgreFromEnv()
                enableLiquibase(changeLogFile)
            }
        }
//        transaction { SchemaUtils.create(ArticleEntity) } se non uso liquibase
    }

    @AfterAll
    internal fun tearDown() {
        app.close()
    }

    @BeforeEach
    internal fun before(){
        exposedArticlesRepository.reset()
    }

    private fun withExpected(test: (articles: List<Article>) -> Unit): Unit{
        transaction {
            initialArticles.map { ArticleDao.new {
                title = it.title
                body = it.body
            } }
                .map (::toArticle)
                .run(test)
        }
    }

    @Test
    fun `test getAll from repo`() {
        withExpected { expectedArticles ->
            exposedArticlesRepository.getAll() shouldBe expectedArticles
        }
    }
}