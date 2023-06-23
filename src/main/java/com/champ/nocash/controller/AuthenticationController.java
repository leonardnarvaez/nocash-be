package com.champ.nocash.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {
    @GetMapping("/register")
    public List<String> getList() {
        return Arrays.asList("leonard", "jon", "jayvee");
    }
}
