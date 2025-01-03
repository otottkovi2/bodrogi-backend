package hu.almokatepitunk.backend

import hu.almokatepitunk.backend.dtos.UserDto
import hu.almokatepitunk.backend.models.User
import hu.almokatepitunk.backend.repos.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BackendUserTests {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Autowired
    lateinit var userRepository: UserRepository

    @BeforeEach
    fun setup() {
        userRepository.deleteAll()
        val user = User("677806696c33cb173dbbd598","new user",
            "\$2a\$10\$yOEQM1d/3Himz3XI7HKrResEzNqK9n7O9l81aTGI4OSLeKdcz24x2","")
        userRepository.save(user)
    }

    @Test
    @DirtiesContext
    fun registerNewUser(){
        userRepository.deleteAll()
        val userToCreate = UserDto("new user","password")
        val response = testRestTemplate.postForEntity<Void>("/api/register",userToCreate)
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        val location = response.headers.location
        assertThat(location).isNotNull()
    }

    @Test
    @DirtiesContext
    fun dontRegisterExistingUser(){
        val userToCreate = UserDto("new user","pasword")
        val response = testRestTemplate.postForEntity<Void>("/api/register",userToCreate)
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }
}