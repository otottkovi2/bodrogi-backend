package hu.almokatepitunk.backend.models

import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("orders")
data class Order(
    val name: String,
    val phone: String,
    val email: String,
    val deadline: Date,
    val description: String
)
