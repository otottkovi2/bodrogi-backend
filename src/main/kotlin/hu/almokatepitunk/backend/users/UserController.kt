package hu.almokatepitunk.backend.users

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
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

    @GetMapping("/api/user/{username}")
    fun getUserByUsername(@PathVariable("username") username: String): ResponseEntity<UserDto> {
        try {
            val user = userService.getUserByUsername(username)
            return ResponseEntity.ok(user)
        } catch (e:UsernameNotFoundException) {
            return ResponseEntity.notFound().build()
        } catch (e:Exception) {
            return ResponseEntity.internalServerError().build()
        }
    }

    @PutMapping("/api/user/{username}")
    fun updateUser(@PathVariable("username") username: String, @RequestBody userDto: UserDto): ResponseEntity<Void> {
        try {
            userService.updateUser(username, userDto)
            return ResponseEntity.ok().build()
        } catch (e:UsernameNotFoundException) {
            return ResponseEntity.notFound().build()
        } catch (e:UserService.UsernameConflictException){
            return ResponseEntity.badRequest().build()
        } catch (e:Exception) {
            return ResponseEntity.internalServerError().build()
        }

    }

    @DeleteMapping("/api/user/{username}")
    fun deleteUserById(@PathVariable("username") usernameToDelete:String): ResponseEntity<String> {
        try {
            val deletedId = userService.deleteUser(usernameToDelete)
            return ResponseEntity.ok(deletedId)
        } catch (e:UsernameNotFoundException) {
            return ResponseEntity.notFound().build()
        } catch (e:Exception) {
            return ResponseEntity.internalServerError().build()
        }
    }
}