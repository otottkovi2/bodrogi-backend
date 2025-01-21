package hu.almokatepitunk.backend.orders

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("orders")
data class Order(
    @Id val id: String = UUID.randomUUID().toString(),
    val name: String,
    val phone: String,
    val email: String,
    val deadline: Date = Date(),
    val description: String = ""
)
