package cn.beginor.demo.api

import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/account")
class AccountController {

    @GetMapping("/info")
    fun getInfo(auth: Authentication?): String {
        return if (auth == null) {
            "anonymous"
        } else {
            "Hello, user ${auth.name}!"
        }
    }
}
