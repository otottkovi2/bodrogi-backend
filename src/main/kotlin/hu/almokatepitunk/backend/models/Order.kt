package hu.almokatepitunk.backend.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Document("orders")
data class Order(
    @Id val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val deadline: Date,
    val description: String
)
