package cn.beginor.demo.api

import cn.beginor.demo.models.LoginModel
import cn.beginor.demo.security.JwtUtil
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/account")
class AccountController(val authManager: AuthenticationManager, val jwtUtil: JwtUtil) {

    @GetMapping("")
    fun getInfo(auth: Authentication?): String {
        return if (auth == null || !auth.isAuthenticated) {
            "anonymous"
        }
        else {
            "hello, ${auth.name} !"
        }
    }

    @PostMapping("")
    fun login(@RequestBody model: LoginModel, request: HttpServletRequest): ResponseEntity<String> {
        return try {
            val token = UsernamePasswordAuthenticationToken(model.username, model.password)
            val auth = authManager.authenticate(token)
            if (auth.isAuthenticated) {
                val user = auth.principal as UserDetails
                val jwt = jwtUtil.generateToken(user, request.contextPath)
                ResponseEntity.ok().body(jwt)
            } else {
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
            }
        } catch (ex: BadCredentialsException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }
}
