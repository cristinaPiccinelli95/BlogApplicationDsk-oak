package com.cgm.experiments.blogapplicationdsl.integration

import com.cgm.experiments.blogapplicationdsl.domain.model.Article
import com.cgm.experiments.blogapplicationdsl.doors.outbound.entities.ArticleDao
import com.cgm.experiments.blogapplicationdsl.doors.outbound.entities.CommentDao
import com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories.exposed.ExposedArticlesRepository
import com.cgm.experiments.blogapplicationdsl.enableLiquibase
import com.cgm.experiments.blogapplicationdsl.helpers.HelperTests
import com.cgm.experiments.blogapplicationdsl.helpers.HelperTests.connectToPostgres
import com.cgm.experiments.blogapplicationdsl.helpers.MyPostgresContainer
import com.cgm.experiments.blogapplicationdsl.start
import com.cgm.experiments.blogapplicationdsl.utilities.ServerPort
import com.cgm.experiments.blogapplicationdsl.utilities.toArticle
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.support.beans
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
class ExposedArticlesRepositoryTest {

    private lateinit var app: ConfigurableApplicationContext

    private val initialArticles = HelperTests.articles
    private val initialComments = HelperTests.comments

    private val exposedArticlesRepository = ExposedArticlesRepository()

    private val changeLogFile = "classpath:/liquibase/db-changelog-master.xml"

    companion object{
        @Container
        val container = MyPostgresContainer.container
    }


    @BeforeAll
    internal fun setUp() {
        app = start(ServerPort()){
            beans {
                connectToPostgres(container)
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

    private fun withExpected(test: (articles: List<Article>) -> Unit){
        transaction {
            val articles = initialArticles.map {
                ArticleDao.new {
                    title = it.title
                    body = it.body
                } }
                .map (::toArticle)

            initialComments.map { comm ->
                CommentDao.new {
                    comment = comm.comment
                    article = ArticleDao.all().first{it.title == comm.article.title}
                    }
            }

            test(articles)
        }
    }

    @Test
    fun `test getAll from repo`() =
        withExpected { expectedArticles ->
            exposedArticlesRepository.getAll() shouldBe expectedArticles
        }


    @Test
    fun `test getOne from repo`() =
        withExpected { expectedArticles ->
            val expected = expectedArticles.first()
            exposedArticlesRepository.getOne(expected.id) shouldBe expected
        }

    @Test
    fun `test save new in repo`() =
        withExpected { expectedArticles ->
            val newArticle = Article(0, "article a", "body of a")
            val newId = expectedArticles.maxByOrNull { it.id }?.let { it.id + 1 } ?: 1
            val expected = newArticle.copy(id = newId)
            exposedArticlesRepository.save(newArticle) shouldBe expected
        }

    @Test
    fun `test delete all`() =
        withExpected {
            exposedArticlesRepository.deleteAll() shouldBe emptyList()
        }

//    @Test
//    fun `test delete one`() =
//        withExpected { expectedArticles ->
//            val expected = listOf(expectedArticles[0], expectedArticles[2] )
//            val articleToDelete = expectedArticles[1]
//            exposedArticlesRepository.deleteOne(articleToDelete.id) shouldBe expected
//        }

}