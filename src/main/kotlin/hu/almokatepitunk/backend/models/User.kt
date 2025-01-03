package hu.almokatepitunk.backend.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("users")
data class User(val username: String, val passwordHash: String, val salt: String){
    @Id
    lateinit var id: String
    constructor(id:String,username:String, passwordHash:String, salt:String) : this(username, passwordHash, salt) {
        this.id = id
    }
}
