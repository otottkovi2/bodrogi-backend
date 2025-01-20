package hu.almokatepitunk.backend.users

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document("users")
data class User(@Id val id:String, val username: String, val passwordHash: String, val salt: String) {
    constructor(username:String ,passwordHash: String,salt: String) : this(UUID.randomUUID().toString(), username,
        passwordHash, salt)
}
