package hu.almokatepitunk.backend

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BackendServerTests {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Test
    fun getHomePage(){
        val response = testRestTemplate.getForEntity<String>("/")
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.headers.contentType).isEqualTo(MediaType.parseMediaType(
            "text/html;charset=utf-8"))
        val body = response.body
        assertThat(body).startsWith("<!doctype html>")
        assertThat(body).contains("It works!")
    }

}