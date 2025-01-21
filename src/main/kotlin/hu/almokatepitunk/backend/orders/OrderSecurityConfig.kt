package hu.almokatepitunk.backend.orders

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class OrderSecurityConfig {

    @Bean
    @Order(2)
    fun orderSecurityFilters(http: HttpSecurity): SecurityFilterChain {
        with(http) {
            securityMatcher("/api/order/**")
            authorizeHttpRequests {
                it.requestMatchers("/api/order/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            }
        }
        return http.build()
    }
}