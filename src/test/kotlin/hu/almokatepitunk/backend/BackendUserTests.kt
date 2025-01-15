package hu.almokatepitunk.backend

import hu.almokatepitunk.backend.users.UserDto
import hu.almokatepitunk.backend.users.User
import hu.almokatepitunk.backend.users.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.Example
import org.springframework.http.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.annotation.DirtiesContext
import org.springframework.util.LinkedMultiValueMap

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BackendUserTests {

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate

    @Autowired
    lateinit var userRepository: UserRepository

    private val testUser = User(
        "new user",
        "{bcrypt}\$2a\$10\$57FYrXITZfEeeIQ/7oGshuEkSQsMwiAziHINixZwfPZJH8658jPWS", "")

    @BeforeEach
    fun setup() {
        if(userRepository.exists(Example.of(testUser))) return
        userRepository.save(testUser)
    }

    @Test
    @DirtiesContext
    fun registerNewUser(){
        userRepository.deleteAll()
        val userToCreate = UserDto(testUser.username,"password")
        val response = testRestTemplate.postForEntity<Void>("/api/register",userToCreate)
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        val location = response.headers.location
        assertThat(location).isNotNull()
    }

    @Test
    @DirtiesContext
    fun dontRegisterExistingUser(){
        val userToCreate = UserDto(testUser.username,"pasword")
        val response = testRestTemplate.postForEntity<Void>("/api/register",userToCreate)
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun getAllUsers(){
        val authHeaders = login()
        val response = testRestTemplate.getForEntity<List<UserDto>>("/api/user",HttpEntity<Void>(authHeaders))
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val userCount = response.body?.size
        assertThat(userCount).isGreaterThan(0)
    }

    @Test
    fun getUserById(){
        val response = testRestTemplate.getForEntity<UserDto>("/api/user/${testUser.username}")
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val dto = response.body
        assertThat(dto).isNotNull()
        val user = userRepository.findByUsername(dto!!.username)
        assertThat(user).isEqualTo(testUser)
    }

    @Test
    fun dontGetNonExistingUser(){
        val response = testRestTemplate.getForEntity<UserDto>("/api/user/fak")
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    @DirtiesContext
    fun putExistingUser(){
        val userToUpdate = UserDto(testUser.username,"fak")
        val putResponse = testRestTemplate.exchange<Void>("/api/user/${userToUpdate.username}", HttpMethod.PUT,
            HttpEntity(userToUpdate))
        assertThat(putResponse.statusCode).isEqualTo(HttpStatus.OK)

        val getResponse = testRestTemplate.getForEntity<UserDto>("/api/user/${userToUpdate.username}")
        assertThat(getResponse.statusCode).isEqualTo(HttpStatus.OK)
        val dto = getResponse.body
        assertThat(dto).isNotNull()
        val newUser = userRepository.findByUsername(dto!!.username)
        assertThat(newUser).isNotEqualTo(testUser)
    }

    @Test
    @DirtiesContext
    fun dontPutNonExistingUser(){
        val userToUpdate = UserDto("fak",testUser.passwordHash)
        val putResponse = testRestTemplate.exchange<Void>("/api/user/fak", HttpMethod.PUT,HttpEntity(userToUpdate))
        assertThat(putResponse.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    @DirtiesContext
    fun dontPutUserWithWrongId(){
        val userToUpdate = UserDto(testUser.username,testUser.passwordHash)
        val putResponse = testRestTemplate.exchange<Void>(
            "/api/user/old user", HttpMethod.PUT,
            HttpEntity(userToUpdate))
        assertThat(putResponse.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)

        val getResponse = testRestTemplate.getForEntity<UserDto>("/api/user/${userToUpdate.username}")
        assertThat(getResponse.statusCode).isEqualTo(HttpStatus.OK)
        val dto = getResponse.body
        assertThat(dto).isNotNull()
        val user = userRepository.findByUsername(dto!!.username)!!
        assertThat(user).isEqualTo(testUser)
    }

    @Test
    @DirtiesContext
    fun deleteExistingUser(){
        val response = testRestTemplate.exchange<Void>("/api/user/${testUser.username}", HttpMethod.DELETE)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val getResponse = testRestTemplate.getForEntity<UserDto>("/api/user/${testUser.username}")
        assertThat(getResponse.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        val dto = getResponse.body
        assertThat(dto).isNull()
    }

    @Test
    @DirtiesContext
    fun dontDeleteNonExistingUser(){
        val putResponse = testRestTemplate.exchange<Void>("/api/user/fak", HttpMethod.DELETE)
        assertThat(putResponse.statusCode).isEqualTo(HttpStatus.NOT_FOUND)

        val getResponse = testRestTemplate.getForEntity<UserDto>("/api/user/${testUser.username}")
        assertThat(getResponse.statusCode).isEqualTo(HttpStatus.OK)
        val dto = getResponse.body
        assertThat(dto).isNotNull()
    }

    private fun login(): HttpHeaders {

        fun getCsrfToken(getResponse: ResponseEntity<String>) =
            getResponse.headers.getFirst("set-cookie")?.split(Regex("(;\\s\\w+)*="))
                ?.get(1) ?: throw CsrfNotProvidedException()

        val getResponse = testRestTemplate.getForEntity<String>("/login")
        val csrfToken = getCsrfToken(getResponse)

        val loginData = LinkedMultiValueMap<String, String>().apply {
            add("username", testUser.username)
            add("password", "password")
            add("_csrf",csrfToken)
        }
        val headers = HttpHeaders()
            headers.add("X-XSRF-TOKEN",csrfToken)
            headers.add("Cookie","XSRF-TOKEN=$csrfToken; Path=/")
        val postResponse = testRestTemplate.postForEntity<String>("/login",
            HttpEntity(loginData,headers)
        )
        assertThat(postResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(postResponse.body).contains("Homepage")
        return headers
    }



    private class CsrfNotProvidedException : RuntimeException()

    @Bean
    fun testRestTemplate(): TestRestTemplate{
        return TestRestTemplate(TestRestTemplate.HttpClientOption.ENABLE_COOKIES)
    }
}