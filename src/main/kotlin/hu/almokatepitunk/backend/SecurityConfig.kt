package hu.almokatepitunk.backend

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun baseFilterChain(http: HttpSecurity): SecurityFilterChain {
        with(http) {

            authorizeHttpRequests {
                it.requestMatchers("/index.html").permitAll()
                    .anyRequest().denyAll()
            }
            csrf { it.disable() }
            cors { it.disable() }
        }
        return http.build()
    }
}