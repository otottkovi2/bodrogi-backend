package hu.almokatepitunk.backend.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity

@Configuration
class SecurityConfig {

//    @Bean
//    fun securityFilterChain(http: HttpSecurity): HttpSecurity {
//        http.authorizeHttpRequests { auth ->
//            auth.requestMatchers("/admin/**").hasRole("ADMIN")
//        }
//
//        return http.build()
//    }
}