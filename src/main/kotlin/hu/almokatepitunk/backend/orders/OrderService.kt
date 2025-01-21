package hu.almokatepitunk.backend.orders

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class OrderService {

    @Autowired
    lateinit var orderRepository: OrderRepository

    fun createOrder(orderToCreate: OrderDto): String {
        val order = dtoToModel(orderToCreate)
        val finalOrder = orderRepository.save(order)
        return finalOrder.id
    }

    fun getAllOrders(pageable: Pageable): Page<OrderDto> {
        val orders = orderRepository.findAll(pageable)
        val dtos = orders.map {
            modelToDto(it)
        }
        return dtos
    }

    fun getOrdersBy(filter: OrderDto,pageable: Pageable): Page<OrderDto>  {
        val filterOrder = dtoToModel(filter)
        val matcher = ExampleMatcher.matchingAll().withIgnorePaths("id")
        val example = Example.of(filterOrder,matcher)
        val orders = orderRepository.findBy(example) { f ->
            f.page(pageable)
        }
        val dtos = orders.map {
            modelToDto(it)
        }
        return dtos
    }

    fun getOrderById(id:String): Optional<OrderDto> {
        val order = orderRepository.findById(id)
        var dto: Optional<OrderDto> = Optional.empty()
        order.ifPresent() {
           dto =  Optional.of(modelToDto(it))
        }
        return dto
    }

    fun updateOrder(id: String, newOrder: OrderDto): Optional<OrderDto> {
        val oldOrder = orderRepository.findById(id)
        var newDto:Optional<OrderDto> = Optional.empty()
        oldOrder.ifPresent() {
            val copiedOrder = it.copy(name = newOrder.name, phone = newOrder.phone, email = newOrder.email,
                deadline = newOrder.deadline, description = newOrder.description)
            orderRepository.save(copiedOrder)
            newDto = Optional.of(modelToDto(copiedOrder))
        }
        return newDto
    }

    fun deleteOrder(id: String): Optional<String> {
        orderRepository.deleteById(id)
        val isOrderDeleted = orderRepository.existsById(id)
        val deletedId:Optional<String> = if (isOrderDeleted) Optional.of(id) else Optional.empty()
        return deletedId
    }

    private fun dtoToModel(dto: OrderDto): Order {
        val order = Order(
            name = dto.name, phone = dto.phone, email = dto.email, deadline = dto.deadline,
            description = dto.description
        )
        return order
    }

    private fun modelToDto(model: Order):OrderDto {
        val dto = OrderDto(
            name = model.name, phone = model.phone, email = model.email, deadline = model.deadline,
            description = model.description
        )
        return dto
    }
}