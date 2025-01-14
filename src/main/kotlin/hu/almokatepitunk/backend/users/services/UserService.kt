package hu.almokatepitunk.backend.users.services

import hu.almokatepitunk.backend.users.User
import hu.almokatepitunk.backend.users.UserDto
import hu.almokatepitunk.backend.users.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService{
    @Autowired
    private lateinit var userRepository: UserRepository
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    fun createUser(user: UserDto): String{
        if(checkExistingUser(user.username)) throw IllegalArgumentException("User already exists")
        var userToSave = User("","","")
        val passwordHash = passwordEncoder.encode(user.password)
        userToSave = userToSave.copy(username = user.username, passwordHash = passwordHash)
        userRepository.save(userToSave)
        val savedUser = userRepository.findByUsername(user.username)
        if(savedUser == null) throw UsernameNotFoundException("Username not found")
        else return savedUser.username
    }

    fun getAllUsers(): List<UserDto> {
        val users = userRepository.findAll()
        val dtos = users.map {
            UserDto(it.username,it.passwordHash)
        }
        return dtos
    }

    fun getUserByUsername(username:String): UserDto {
        val possibleUser = userRepository.findByUsername(username) ?:
        throw UsernameNotFoundException("Username not found")
        val dto = UserDto(possibleUser.username, possibleUser.passwordHash)
        return dto
    }

    fun updateUser(usernameToUpdate: String, userDto: UserDto): UserDto {
        if(usernameToUpdate != userDto.username) throw UsernameConflictException("The two usernames do not match.")
        if(!checkExistingUser(userDto.username)) throw UsernameNotFoundException("Username not found")
        val user = userRepository.findByUsername(userDto.username)!!
        val userToUpdate = User(userDto.username,passwordEncoder.encode(userDto.password),user.salt)
        val updatedUser = userRepository.save(userToUpdate)
        val dto = userDto.copy(password = updatedUser.passwordHash)
        return dto
    }

    fun deleteUser(username:String): String {
        if(!checkExistingUser(username)) throw UsernameNotFoundException("Username not found")
        val user = userRepository.findByUsername(username)!!
        userRepository.delete(user)
        return user.username
    }

    private fun checkExistingUser(username:String): Boolean{
        return userRepository.findByUsername(username) != null
    }

    class UsernameConflictException(message:String): Exception()
}