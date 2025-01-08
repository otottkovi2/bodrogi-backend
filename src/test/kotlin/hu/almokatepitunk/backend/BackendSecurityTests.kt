package hu.almokatepitunk.backend

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BackendSecurityTests {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Test
    fun getAdminPageWithAuthorization(){
        val response = testRestTemplate.withBasicAuth("new user","password")
            .getForEntity<String>("/admin")
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun dontGetAdminPageWithoutAuthorization() {
        val response = testRestTemplate.getForEntity<String>("/admin")
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).contains("Login")
    }

}