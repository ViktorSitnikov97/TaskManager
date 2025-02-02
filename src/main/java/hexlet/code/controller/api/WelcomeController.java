package hexlet.code.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public final class WelcomeController {
    @GetMapping(path = "/welcome")
    @ResponseStatus(HttpStatus.OK)
    public String greeting() {
        return "Welcome to Spring Boot!";
    }
}
