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
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BackendUserTests {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Autowired
    lateinit var userRepository: UserRepository

    private val testUser = User(
        "677806696c33cb173dbbd598", "new user",
        "\$2a\$10\$yOEQM1d/3Himz3XI7HKrResEzNqK9n7O9l81aTGI4OSLeKdcz24x2", "")

    @BeforeEach
    fun setup() {
        userRepository.deleteAll()
        val user = testUser
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

    @Test
    fun getAllUsers(){
        val response = testRestTemplate.getForEntity<List<UserDto>>("/api/user")
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val userCount = response.body?.size
        assertThat(userCount).isEqualTo(1)
    }

    @Test
    fun getUserById(){
        val response = testRestTemplate.getForEntity<UserDto>("/api/user/677806696c33cb173dbbd598")
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
        var user = testUser.copy(salt="NaCl")
        val putResponse = testRestTemplate.exchange<Void>("/api/user/${user.id}", HttpMethod.PUT,HttpEntity(user))
        assertThat(putResponse.statusCode).isEqualTo(HttpStatus.OK)

        val getResponse = testRestTemplate.getForEntity<UserDto>("/api/user/${user.id}")
        assertThat(getResponse.statusCode).isEqualTo(HttpStatus.OK)
        val dto = getResponse.body
        assertThat(dto).isNotNull()
        user = userRepository.findByUsername(dto!!.username)!!
        assertThat(user).isNotEqualTo(testUser)
    }

    @Test
    @DirtiesContext
    fun dontPutNonExistingUser(){
        var user = testUser.copy(salt="NaCl")
        val putResponse = testRestTemplate.exchange<Void>("/api/user/fak", HttpMethod.PUT,HttpEntity(user))
        assertThat(putResponse.statusCode).isEqualTo(HttpStatus.NOT_FOUND)

        val getResponse = testRestTemplate.getForEntity<UserDto>("/api/user/${user.id}")
        assertThat(getResponse.statusCode).isEqualTo(HttpStatus.OK)
        val dto = getResponse.body
        assertThat(dto).isNotNull()
        user = userRepository.findByUsername(dto!!.username)!!
        assertThat(user).isEqualTo(testUser)
    }

    @Test
    @DirtiesContext
    fun dontPutUserWithWrongId(){
        var user = testUser.copy(salt="NaCl")
        val putResponse = testRestTemplate.exchange<Void>("/api/user/677806696cffff173dbbd598", HttpMethod.PUT,
            HttpEntity(user))
        assertThat(putResponse.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)

        val getResponse = testRestTemplate.getForEntity<UserDto>("/api/user/${user.id}")
        assertThat(getResponse.statusCode).isEqualTo(HttpStatus.OK)
        val dto = getResponse.body
        assertThat(dto).isNotNull()
        user = userRepository.findByUsername(dto!!.username)!!
        assertThat(user).isEqualTo(testUser)
    }

    @Test
    @DirtiesContext
    fun deleteExistingUser(){
        val response = testRestTemplate.exchange<Void>("/api/user/${testUser.id}", HttpMethod.DELETE)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val getResponse = testRestTemplate.getForEntity<UserDto>("/api/user/${testUser.id}")
        assertThat(getResponse.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        val dto = getResponse.body
        assertThat(dto).isNull()
    }

    @Test
    @DirtiesContext
    fun dontDeleteNonExistingUser(){
        val putResponse = testRestTemplate.exchange<Void>("/api/user/fak", HttpMethod.DELETE)
        assertThat(putResponse.statusCode).isEqualTo(HttpStatus.NOT_FOUND)

        val getResponse = testRestTemplate.getForEntity<UserDto>("/api/user/${testUser.id}")
        assertThat(getResponse.statusCode).isEqualTo(HttpStatus.OK)
        val dto = getResponse.body
        assertThat(dto).isNotNull()
    }
}