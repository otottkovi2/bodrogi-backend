package hu.almokatepitunk.backend.users

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.csrf.CookieCsrfTokenRepository

@Configuration
@EnableWebSecurity
class UserSecurityConfig {

    @Autowired
    private lateinit var userDetailsService: UserDetailsService

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Bean
    fun useCustomAuthenticationManager(): AuthenticationManager {
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
                it.loginProcessingUrl( "/login").permitAll()
            }
            authenticationManager(useCustomAuthenticationManager())
            securityContext {
                it.requireExplicitSave(false)
            }
            csrf {
                it.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                it.csrfTokenRequestHandler(CookieCsrfTokenRequesthandler())
            }
            cors { it.disable() }
        }
        return http.build()
    }
}