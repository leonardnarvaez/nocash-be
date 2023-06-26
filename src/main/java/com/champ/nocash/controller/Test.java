package com.champ.nocash.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api")
public class Test {
    @GetMapping(value = "/employees")
    public ResponseEntity<?> getAllEmployees() {
        return new ResponseEntity<>(Arrays.asList("jon", "leonard", "jayvee"), HttpStatus.ACCEPTED);
    }
}
