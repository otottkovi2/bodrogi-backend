package hu.almokatepitunk.backend.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

     @Bean
     fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
         with(http) {
             //TODO: turn security back on
             authorizeHttpRequests { it.anyRequest().permitAll() }
             csrf { it.disable() }
             cors { it.disable() }
         }
         return http.build()
     }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}