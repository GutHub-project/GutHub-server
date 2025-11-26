package com.guthub.guthubserver.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/login/success")
    public ResponseEntity<String> success() {
        return ResponseEntity.ok("login success");
    }

}
