package hu.almokatepitunk.backend

import hu.almokatepitunk.backend.users.User
import jakarta.servlet.http.Cookie
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class BackendSecurityTests() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var userDetailsService: UserDetailsService

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @BeforeEach
    fun setup() {
        mockMvc.perform(post("/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"username\": \"someone else\", \"password\": \"secure password\" }")
        ).andReturn()
    }

    @Test
    @WithMockUser(username = "someone else", password = "secure password" ,roles = ["ADMIN"])
    fun getAdminPageWithAuthorization() {
        val result = mockMvc.perform(get("/admin")).andReturn()
        assertThat(result.response.status).isEqualTo(HttpStatus.OK.value())
        assertThat(result.response.forwardedUrl).contains("admin/index.html")
    }

    @Test
    fun dontGetAdminPageWithoutAuthorization() {
        val result = mockMvc.perform(get("/admin")).andReturn()
        assertThat(result.response.status).isEqualTo(HttpStatus.FOUND.value())
        assertThat(result.response.redirectedUrl).contains("/login")
    }

    @Test
    fun postLogin(){
        val result = mockMvc.perform(formLogin("/login")
            .user("someone else")
            .password("secure password"))
            .andReturn()
        assertThat(result.response.status).isEqualTo(HttpStatus.FOUND.value())
        assertThat(result.response.redirectedUrl).isEqualTo("/")
    }

    @Test
    fun findExistingUser(){
        val userToFind = User("someone else",
            "{bcrypt}\$2a\$10\$MbuZd4HEbUk03Ej6sZS8DufP2BkxTkVuYIH9KaHqGYqAB/amDreHu", "")
        val user = userDetailsService.loadUserByUsername(userToFind.username)
        assertThat(user.username).isEqualTo(userToFind.username)
        assertThat(user.password).isEqualTo(userToFind.passwordHash)
    }

    @Test
    fun authenticateExistingUser() {
        val auth = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken("someone else", "secure password")
        )
        assertThat(auth.isAuthenticated).isTrue()
    }

    @Test
    fun dontAuthenticateNonExistingUser() {
        try {
            val auth = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken("an alien", "secure password")
            )
            assertThat(auth.isAuthenticated).isFalse()
        } catch (e: BadCredentialsException) {
            assertThat(true).isTrue()
        }

    }

    @Test
    fun dontAuthenticateExistingUserWithBadCredentials() {
        try {
            val auth = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken("someone else", "secure, but wrong password")
            )
            assertThat(auth.isAuthenticated).isFalse()
        } catch (e: BadCredentialsException) {
            assertThat(true).isTrue()
        }
    }

}