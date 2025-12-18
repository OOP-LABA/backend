package org.uneev.charityboard.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class TestController {

    @GetMapping("/test")
    public String testEndpoint() {
        return "Hola";
    }

    @GetMapping("/secured")
    public String securedEndpoint(Principal principal) {
        return "(SECURED) Hola! " + principal.getName();
    }
}
