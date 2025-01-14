package hu.almokatepitunk.backend

import hu.almokatepitunk.backend.users.User
import hu.almokatepitunk.backend.users.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest
class BackendRepoTests {

    @Autowired
    lateinit var repo: UserRepository

    val testUser = User("admin","gsrnjhdetöinfbóíúdf",
        "NaCl")

    @BeforeEach
    fun setup() {
        repo.deleteAll()
        repo.save(testUser)
    }

    @Test
    fun readAllUsers(){
        val users = repo.findAll()
        assertThat(users).hasSizeGreaterThan(0)
    }

    @Test
    fun readUser(){
        val user = repo.findByUsername("admin")
        assertThat(user).isNotNull
        assertThat(user).isEqualTo(testUser)
    }

    @Test
    @DirtiesContext
    fun addUser(){
        repo.deleteAll()
        val response = repo.save(testUser)
        assertThat(response).isEqualTo(testUser)
    }

    @Test
    @DirtiesContext
    fun deleteUser(){
        repo.delete(testUser)
        val users = repo.findAll()
        assertThat(users).containsExactlyInAnyOrder()
    }

    @Test
    @DirtiesContext
    fun updateUser(){
        val newUser = testUser.copy(passwordHash = "fak")
        val user = repo.save(newUser)
        assertThat(user).isEqualTo(newUser)
        assertThat(user).isNotEqualTo(testUser)
    }

}
