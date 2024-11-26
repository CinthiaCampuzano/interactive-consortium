package com.utn.interactiveconsortium.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @PostMapping(path = "/public")
    public String postPublic() {
        return "Access to 'POST /public' granted";
    }

    @GetMapping(path = "/public")
    public String getPublic() {
        return "Access to 'GET /public' granted";
    }

    @GetMapping(path = "/secured")
    public String secured() {
        return "Access to '/secured' granted";
    }

    @GetMapping(path = "/user")
    public String user() {
        return "Access to '/user' granted";
    }

    @GetMapping(path = "/admin")
    public String admin() {
        return "Access to '/admin' granted";
    }
}