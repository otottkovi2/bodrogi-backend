package hu.almokatepitunk.backend.orders

import java.util.*

data class OrderDto(val name: String,
                    val phone: String,
                    val email: String,
                    val deadline: Date,
                    val description: String)
