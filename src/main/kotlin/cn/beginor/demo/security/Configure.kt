package cn.beginor.demo.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.ObjectPostProcessor
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
class Configure {

    @Bean
    fun authManagerBean(): AuthenticationManager {
        val handler = object : ObjectPostProcessor<Any?> {
            override fun <O> postProcess(obj: O): O {
                return obj
            }
        }
        // demo user only
        val user = User.withDefaultPasswordEncoder()
            .username("admin")
            .password("admin")
            //.roles("admins", "users")
            .authorities("ROLE_admins", "ROLE_users", "users:read", "users:update", "users:delete")
            .build()
        val builder = AuthenticationManagerBuilder(handler)
            builder.inMemoryAuthentication()
                   .withUser(user)
        return builder.build()
    }

    @Bean
    fun securityFilterChainBean(http: HttpSecurity): SecurityFilterChain {
        http.csrf().disable()
        http.formLogin().disable()
        return http.build()
    }
}
