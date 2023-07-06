package com.champ.nocash.controller;

import com.champ.nocash.service.AuthenticationHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authentication-history")
public class AuthenticationHistoryController {
    @Autowired
    private AuthenticationHistoryService authenticationHistoryService;
    @GetMapping("/")
    public ResponseEntity<?> getAuthenticationHistory() {
        return ResponseEntity.ok(authenticationHistoryService.findAll());
    }
}
