package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.utils.FileUtils.loadFileFromClasspath;

@RestController
public class SwaggerController {

    @GetMapping("/limit-service-rest-v1.yaml")
    public String productServiceRestV1() {
        return loadFileFromClasspath("api/limit-service-rest-v1.yaml");
    }

}
