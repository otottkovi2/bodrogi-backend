package hu.almokatepitunk.backend

import hu.almokatepitunk.backend.users.UserDto
import hu.almokatepitunk.backend.users.User
import hu.almokatepitunk.backend.users.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.http.*
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.assertj.MockMvcTester
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.web.context.WebApplicationContext

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureJsonTesters
class BackendUserTests {

    @Autowired
    private lateinit var applicationContext: WebApplicationContext

    private lateinit var mockMvc: MockMvcTester

    @Autowired
    private lateinit var userJsonSerializer: JacksonTester<UserDto>

    @Autowired
    private lateinit var userListJsonSerializer:JacksonTester<List<UserDto>>

    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate

    @Autowired
    lateinit var userRepository: UserRepository

    private val testUser = User(
        "new user",
        "{bcrypt}\$2a\$10\$57FYrXITZfEeeIQ/7oGshuEkSQsMwiAziHINixZwfPZJH8658jPWS", "")

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcTester.from(applicationContext) { config ->
            config.apply<DefaultMockMvcBuilder>(springSecurity())
                .build()
        }
        val exampleMatcher = ExampleMatcher.matching().withIgnorePaths("id","passwordHash")
        if(userRepository.exists(Example.of(testUser, exampleMatcher))) return
        userRepository.save(testUser)
    }

    @Test
    @DirtiesContext
    fun registerNewUser(){
        userRepository.deleteAll()
        val userToCreate = UserDto(testUser.username,"password")
        val userJson = userJsonSerializer.write(userToCreate)
        val result = mockMvc.post()
            .uri("/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(userJson.json)
            .exchange()
        assertThat(result.response.status).isEqualTo(HttpStatus.CREATED.value())
        assertThat(result.response.getHeader("Location")).matches("http://.*/api/user/.+")
    }

    @Test
    @DirtiesContext
    fun dontRegisterExistingUser(){
        val userToCreate = UserDto(testUser.username,"pasword")
        val userJson = userJsonSerializer.write(userToCreate)
        val result = mockMvc.post()
            .uri("/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(userJson.json)
            .exchange()
        assertThat(result.response.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    @WithMockUser()
    fun getAllUsers(){
        val result = mockMvc.get()
            .uri("/api/user")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
        assertThat(result.response.status).isEqualTo(HttpStatus.OK.value())
        val users = userListJsonSerializer.parse(result.response.contentAsString).`object`
        assertThat(users).isNotNull
        assertThat(users.size).isGreaterThan(0)
    }

    @Test
    @WithMockUser()
    fun getUserById(){
        val result = mockMvc.get()
            .uri("/api/user/{username}",testUser.username)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
        assertThat(result.response.status).isEqualTo(HttpStatus.OK.value())
        val user = userJsonSerializer.parse(result.response.contentAsString).`object`
        assertThat(user).isNotNull
        assertThat(user.username).isEqualTo(testUser.username)
    }

    @Test
    @WithMockUser()
    fun dontGetNonExistingUser(){
        val result = mockMvc.get()
            .uri("/api/user/{username}",testUser.username.plus(" impostor"))
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
        assertThat(result.response.status).isEqualTo(HttpStatus.NOT_FOUND.value())
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