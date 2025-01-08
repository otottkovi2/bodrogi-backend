package hu.almokatepitunk.backend.users

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
class UserSecurityConfig {

    @Autowired
    private lateinit var userDetailsService: UserDetailsService

    @Autowired
    fun configureAuth(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService)
    }

    @Bean
    @Order(1)
    fun adminFilterChain(http: HttpSecurity): SecurityFilterChain {
        with(http) {
            securityMatcher("/admin/**","/login/**")
            authorizeHttpRequests {
                    it.requestMatchers("/admin/**").hasRole("ADMIN")
                    it.requestMatchers("/login/**").permitAll()
                        .anyRequest().authenticated()
                }
            formLogin {
                it.loginPage("/login").permitAll()
            }
            csrf {}
            cors { it.disable() }
        }
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}