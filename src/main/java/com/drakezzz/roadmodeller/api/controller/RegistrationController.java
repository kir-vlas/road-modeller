package com.drakezzz.roadmodeller.api.controller;

import com.drakezzz.roadmodeller.persistence.entity.User;
import com.drakezzz.roadmodeller.service.UserService;
import com.drakezzz.roadmodeller.web.dto.StatusResult;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/registration")
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public StatusResult register(@RequestBody User user) {
        return userService.registerUser(user);
    }

}
