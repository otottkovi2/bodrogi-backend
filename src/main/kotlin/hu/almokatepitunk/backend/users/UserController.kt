package hu.almokatepitunk.backend.users

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@RestController
class UserController {

    @Autowired
    lateinit var userService: UserService

    @PostMapping("/api/register")
    fun register(@RequestBody newUser: UserDto, uriBuilder: UriComponentsBuilder): ResponseEntity<Void> {
        val userId = try {
            userService.createUser(newUser)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().build()
        } catch (e:Exception) {
            return ResponseEntity.internalServerError().build()
        }
        val uri = uriBuilder.path("/api/user/${userId}").build().toUri()
        return ResponseEntity.created(uri).build()
    }

    @GetMapping("/api/user")
    fun getAllUsers(): ResponseEntity<List<UserDto>> {
        val users = userService.getAllUsers()
        return ResponseEntity.ok(users)
    }

    @GetMapping("/api/user/{id}")
    fun getUserById(@PathVariable("id") id: String): ResponseEntity<UserDto> {
        try {
            val user = userService.getUserById(id)
            return ResponseEntity.ok(user)
        } catch (e:IllegalArgumentException) {
            return ResponseEntity.notFound().build()
        } catch (e:Exception) {
            return ResponseEntity.internalServerError().build()
        }
    }

    @PutMapping("/api/user/{id}")
    fun updateUser(@PathVariable("id") id: String,userDto: UserDto): ResponseEntity<Void> {
        try {
            userService.updateUser(id, userDto)
            return ResponseEntity.ok().build()
        } catch (e:IllegalArgumentException) {
            return ResponseEntity.notFound().build()
        } catch (e:UserService.IdConflictException){
            return ResponseEntity.badRequest().build()
        } catch (e:Exception) {
            return ResponseEntity.internalServerError().build()
        }

    }

    @DeleteMapping("/api/user")
    fun deleteUserById(userToDelete: UserDto): ResponseEntity<String> {
        try {
            val deletedId = userService.deleteUser(userToDelete)
            return ResponseEntity.ok(deletedId)
        } catch (e:IllegalArgumentException) {
            return ResponseEntity.notFound().build()
        } catch (e:Exception) {
            return ResponseEntity.internalServerError().build()
        }
    }
}