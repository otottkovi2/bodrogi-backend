package hu.almokatepitunk.backend.orders

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("/api/order")
class OrderController {

    @Autowired
    private lateinit var orderService: OrderService

    @GetMapping
    fun getOrders(pageable: Pageable): ResponseEntity<List<OrderDto>> {
        val orders = orderService.getAllOrders(pageable)
        return ResponseEntity.ok(orders.content)
    }

    @GetMapping("/{id}")
    fun getOrderById(@PathVariable id:String): ResponseEntity<OrderDto> {
        val order = orderService.getOrderById(id)
        return if (order.isPresent) ResponseEntity.ok(order.get())
        else ResponseEntity.notFound().build()
    }

    @GetMapping
    fun getOrdersByFilter(pageable: Pageable,@RequestParam filter:OrderDto): ResponseEntity<List<OrderDto>> {
        val orders = orderService.getOrdersBy(filter,pageable)
        return ResponseEntity.ok(orders.content)
    }

    @PostMapping
    fun createOrder(@RequestBody dto: OrderDto, uriBuilder:UriComponentsBuilder): ResponseEntity<Void> {
        val orderId = orderService.createOrder(dto)
        val orderURI = uriBuilder.path("/api/order/${orderId}").build().toUri()
        return ResponseEntity.created(orderURI).build()
    }

    @PutMapping("/{id}")
    fun updateOrder(@PathVariable id:String, @RequestBody newDto:OrderDto): ResponseEntity<OrderDto> {
        val savedOrder = orderService.updateOrder(id,newDto)
        return if (savedOrder.isPresent) ResponseEntity.ok(savedOrder.get())
        else ResponseEntity.notFound().build()
    }

    @DeleteMapping
    fun deleteOrder(@RequestParam id:String): ResponseEntity<String> {
        val deletedId = orderService.deleteOrder(id)
        return if (deletedId.isPresent) ResponseEntity.ok(id) else ResponseEntity.notFound().build()
    }
}