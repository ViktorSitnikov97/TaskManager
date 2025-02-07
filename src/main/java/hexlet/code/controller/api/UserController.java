package hexlet.code.controller.api;


import hexlet.code.dto.AuthRequest;
import hexlet.code.dto.users.UserCreateDTO;
import hexlet.code.dto.users.UserDTO;
import hexlet.code.dto.users.UserUpdateDTO;
import hexlet.code.service.UserService;
import hexlet.code.utils.UserUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserUtils userUtils;

    @GetMapping(path = "")
    public ResponseEntity<List<UserDTO>> index() {
        var users =  userService.getAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(users.size()))
                .body(users);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO show(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@Valid @RequestBody UserCreateDTO data) {
        return userService.create(data);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@userUtils.isAuthor(#id)")
    public ResponseEntity<UserDTO> update(@Valid @RequestBody UserUpdateDTO data, @PathVariable Long id) {

        var oldEmail = userService.findById(id).getEmail();
        var userDTO = userService.update(data, id);
        var newEmail = userService.findById(id).getEmail();
        String password = null;

        if (data.getPassword() != null) {
            password = data.getPassword().get();
        }
        String responseToken = null;
        if (!oldEmail.equals(newEmail)) {
            AuthRequest authData = new AuthRequest();
            authData.setUsername(newEmail);
            authData.setPassword(password);
            RestClient restClient = RestClient.create();
            responseToken = restClient.post()
                    .uri("http://localhost:8080/api/login")
                    .contentType(APPLICATION_JSON)
                    .body(authData)
                    .retrieve()
                    .body(String.class);
        }
        if (responseToken != null) {
            return ResponseEntity.ok().header("Authorization", responseToken).body(userDTO);
        }
        return ResponseEntity.ok().body(userDTO);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@userUtils.isAuthor(#id)")
    public void delete(@PathVariable Long id) {
        userService.destroy(id);
    }


}
