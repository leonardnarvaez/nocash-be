package com.champ.nocash.controller;

import com.champ.nocash.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api")
public class Test {
    @Autowired
    private SecurityUtil securityUtil;
    @GetMapping(value = "/employees")
    public ResponseEntity<?> getAllEmployees() {
        System.out.println(securityUtil.getUserEntity());
        return new ResponseEntity<>(Arrays.asList("jon", "leonard", "jayvee"), HttpStatus.ACCEPTED);
    }
}
