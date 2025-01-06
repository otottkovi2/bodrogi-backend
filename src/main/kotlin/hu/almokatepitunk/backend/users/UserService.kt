package hu.almokatepitunk.backend.users

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
        else return savedUser.id
    }

    fun getAllUsers(): List<UserDto> {
        val users = userRepository.findAll()
        val dtos = users.map {
            UserDto(it.username,it.passwordHash)
        }
        return dtos
    }

    fun getUserById(id:String): UserDto {
        val optionalUser = userRepository.findById(id)
        if(optionalUser.isEmpty){
            throw IllegalArgumentException("User not found")
        }
        val user = optionalUser.get()
        val dto = UserDto(user.username,user.passwordHash)
        return dto
    }

    fun updateUser(idToUpdate: String, userDto: UserDto): UserDto {
        if(!checkExistingUserById(idToUpdate)) throw IllegalArgumentException("Given id does not exist")
        if(!checkExistingUser(userDto.username)) throw UsernameNotFoundException("Username not found")
        val user = userRepository.findByUsername(userDto.username)!!
        if(idToUpdate != user.id) throw IdConflictException("Given id does not match the user id")
        val updatedUser = userRepository.save(user)
        val dto = userDto.copy(password = updatedUser.passwordHash)
        return dto
    }

    fun deleteUser(userDto: UserDto): String {
        if(!checkExistingUser(userDto.username)) throw UsernameNotFoundException("Username not found")
        val user = userRepository.findByUsername(userDto.username)!!
        userRepository.delete(user)
        return user.id
    }

    private fun checkExistingUser(username:String): Boolean{
        return userRepository.findByUsername(username) != null
    }

    private fun checkExistingUserById(id:String): Boolean{
        return userRepository.findById(id).isPresent
    }

    class IdConflictException(message:String): Exception()
}