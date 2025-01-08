package hu.almokatepitunk.backend.users.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class LoginController {

    @GetMapping("login")
    fun login():String{
        return "login/index.html"
    }
}