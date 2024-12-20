package hu.almokatepitunk.backend.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

     @Bean
     fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
         http.authorizeHttpRequests { auth ->
             auth.requestMatchers("/**")
                 .permitAll()
                 .anyRequest()
                 .permitAll()
         }
 
         return http.build()
     }
}