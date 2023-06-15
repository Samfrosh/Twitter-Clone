package com.samuel.controller;

import com.samuel.dto.request.RegistrationRequest;
import com.samuel.service.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("API/V1/USERS")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/REGISTRATION")
    public String RegisterUser(@RequestBody RegistrationRequest registrationRequest){
        return registrationService.registration(registrationRequest);
    }
}
