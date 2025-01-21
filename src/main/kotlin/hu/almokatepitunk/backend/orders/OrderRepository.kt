package hu.almokatepitunk.backend.orders

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface OrderRepository : MongoRepository<Order, String>, PagingAndSortingRepository<Order, String> {

    fun findByName(name: String,pageRequest: PageRequest): Page<Order>
}