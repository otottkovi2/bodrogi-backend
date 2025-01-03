package hu.almokatepitunk.backend.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Document("orders")
data class Order(
    val name: String,
    val phone: String,
    val email: String,
    val deadline: Date,
    val description: String
) {
    @Id
    lateinit var id: String
    constructor(id:String,name:String, phone:String, email:String, deadline: Date,description: String) :
            this(name, phone, email, deadline, description) {
                this.id = id
            }
}
