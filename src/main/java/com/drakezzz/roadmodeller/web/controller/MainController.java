package com.drakezzz.roadmodeller.web.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @ApiOperation("Главная страница")
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @ApiOperation("Страница регистрации нового пользователя")
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @ApiOperation("Форма входа в приложение")
    @GetMapping("login")
    public String login() {
        return "login";
    }
}
