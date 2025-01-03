package hu.almokatepitunk.backend

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BackendServerTests {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Test
    fun getAdmin(){
        val result = testRestTemplate.getForEntity<String>("/admin")
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        val body = result.body
        assertThat(body).startsWith("<!DOCTYPE html>")
        assertThat(body).contains("Admin site")
    }

}