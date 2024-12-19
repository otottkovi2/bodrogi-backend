package hu.almokatepitunk.backend

import hu.almokatepitunk.backend.models.User
import hu.almokatepitunk.backend.repos.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest
class BackendRepoTests {

    @Autowired
    lateinit var repo:UserRepository

    @Test
    fun readAllUsers(){
        val users = repo.findAll()
        assertThat(users).hasSizeGreaterThan(0)
    }

    @Test
    fun readUser(){
        val user = repo.findByUsername("admin")
        val exampleUser = User("6761cd8062d02b0120dff1ba","admin","gsrnjhdetöinfbóíúdf",
            "NaCl")
        assertThat(user).isEqualTo(exampleUser)
    }

    @Test
    @DirtiesContext
    fun addUser(){
        val user = User("6761cd8062d02b0120dff1ba","admin","gsrnjhdetöinfbóíúdf",
            "NaCl")
        val response = repo.save(user)
        assertThat(response).isEqualTo(user)
    }

    @Test
    @DirtiesContext
    fun deleteUser(){
        val user = User("6761cd8062d02b0120dff1ba","admin","gsrnjhdetöinfbóíúdf",
            "NaCl")
        repo.delete(user)
        val users = repo.findAll()
        assertThat(users).containsExactlyInAnyOrder()
    }

    @Test
    @DirtiesContext
    fun updateUser(){
        val newUser = User("6761cd8062d02b0120dff1ba","admin","gsrnjhdeúdf",
            "NaCl")
        val user = repo.save(newUser)
        assertThat(user).isEqualTo(newUser)
        assertThat(user).isNotEqualTo(User("6761cd8062d02b0120dff1ba","admin",
            "gsrnjhdetöinfbóíúdf", "NaCl"))
    }

}
