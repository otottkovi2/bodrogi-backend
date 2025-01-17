package hu.almokatepitunk.backend

import hu.almokatepitunk.backend.users.UserDto
import hu.almokatepitunk.backend.users.User
import hu.almokatepitunk.backend.users.UserRepository
import hu.almokatepitunk.backend.utils.SecureHttpClient
import hu.almokatepitunk.backend.utils.SecureHttpClient.Companion.getForEntitySecure
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.data.domain.Example
import org.springframework.http.*
import org.springframework.test.annotation.DirtiesContext
import org.springframework.web.context.WebApplicationContext

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BackendUserTests {

    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var applicationContext: WebApplicationContext

    lateinit var httpClient: SecureHttpClient

    private val testUser = User(
        "new user",
        "{bcrypt}\$2a\$10\$57FYrXITZfEeeIQ/7oGshuEkSQsMwiAziHINixZwfPZJH8658jPWS", "")

    @BeforeEach
    fun setup() {
        if (!this::httpClient.isInitialized) httpClient = SecureHttpClient(applicationContext,"new user")
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
        val debugResponse = testRestTemplate.getForEntitySecure(httpClient,"/api/users",String::class.java)
        //todo:find a way to output a lsit type
        val response = testRestTemplate.getForEntitySecure(httpClient,"api/users",String::class.java)
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
}