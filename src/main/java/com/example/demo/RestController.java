package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by Artem Godin on 9/3/2020.
 */
@Controller
public class RestController extends Api {
    @PostMapping("/hello")
    public ResponseEntity<String> hello(@RequestParam("name") String name) {
        return ResponseEntity.ok("Boss sends his greetings, " + name);
    }
}
