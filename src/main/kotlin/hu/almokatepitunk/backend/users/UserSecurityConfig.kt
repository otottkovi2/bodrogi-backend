package hu.almokatepitunk.backend.users

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
class UserSecurityConfig {

    @Autowired
    private lateinit var userDetailsService: UserDetailsService

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Bean
    fun authenticationManager(): AuthenticationManager {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailsService)
        authProvider.setPasswordEncoder(passwordEncoder)
        return ProviderManager(authProvider)
    }

    @Bean
    @Order(1)
    fun adminFilterChain(http: HttpSecurity): SecurityFilterChain {
        with(http) {
            securityMatcher("/admin/**","/login/**","/api/user/**")
            authorizeHttpRequests {
                    it.requestMatchers("/admin/**").hasRole("ADMIN")
                    it.requestMatchers("/api/user/**").authenticated()
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
}