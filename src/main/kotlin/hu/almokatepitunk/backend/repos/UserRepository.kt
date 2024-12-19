package hu.almokatepitunk.backend.repos

import hu.almokatepitunk.backend.models.User
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<User, String> {

    fun findByUsername(username: String):User
}