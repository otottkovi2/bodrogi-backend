package hu.almokatepitunk.backend.users

import org.springframework.data.mongodb.core.mapping.Document

@Document("users")
data class User(val username: String, val passwordHash: String, val salt: String)
