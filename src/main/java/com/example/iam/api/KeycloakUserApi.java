package com.example.iam.api;

import com.example.iam.dto.UserRegistrationRecord;
import com.example.iam.service.KeycloakUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class KeycloakUserApi {

    private final KeycloakUserService keycloakUserService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserRegistrationRecord createUser(@Valid @RequestBody UserRegistrationRecord userRegistrationRecord) {
        return keycloakUserService.createUser(userRegistrationRecord);
    }

    @GetMapping
    public UserRepresentation getUser(Principal principal) {
        return keycloakUserService.getUserById(principal.getName());
    }

    @DeleteMapping("/{userid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable String userid) {
        keycloakUserService.deleteUserById(userid);
    }
}