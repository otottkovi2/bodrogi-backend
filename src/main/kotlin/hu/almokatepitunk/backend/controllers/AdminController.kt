package hu.almokatepitunk.backend.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class AdminController {

    @GetMapping("/admin")
    fun getAdmin():String{
        return "admin/index.html"
    }
}