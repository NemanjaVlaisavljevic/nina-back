package com.nemanjav.back.controller;

import com.nemanjav.back.dto.UserDto;
import com.nemanjav.back.entity.User;
import com.nemanjav.back.http.LoginRequest;
import com.nemanjav.back.http.LoginResponse;
import com.nemanjav.back.service.ConfirmationTokenService;
import com.nemanjav.back.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthenticationController {

    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;
    private final SessionRegistry sessionRegistry;

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(fieldError -> log.error(fieldError.getField() + " : " + fieldError.getDefaultMessage()));
            return ResponseEntity.badRequest().build();
        }
        try {
            return userService.saveUser(userDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/register/confirm")
    public String confirmation(@RequestParam("token") String token) {
        return confirmationTokenService.confirmToken(token);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@RequestBody UserDto userDto, Authentication authentication) {
        return userService.updateUser(userDto, authentication);
    }

    @GetMapping("/profile/{email}")
    public ResponseEntity<User> getUserProfile(@PathVariable String email, Principal principal) {
        try {
            return userService.getCurrentUser(email, principal);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(Authentication authentication) {
        return ResponseEntity.ok(userService.logout(authentication));
    }

    @GetMapping("/getAllLoggedInUsers")
    public List<String> getAllLoggedInUsers() {
        final List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
        System.out.println(allPrincipals);
        List<String> emailsOfCurrentUsers = new ArrayList<>();
        for (final Object principal : allPrincipals) {
            if (principal instanceof org.springframework.security.core.userdetails.User) {
                final User user = (User) principal;

                List<SessionInformation> activeUserSessions =
                        sessionRegistry.getAllSessions(principal,
                                /* includeExpiredSessions */ false); // Should not return null;

                if (!activeUserSessions.isEmpty()) {
                    System.out.println(user);
                    emailsOfCurrentUsers.add(user.getEmail());
                }
            }else{
                System.out.println("EMPTY LIST OF ACTIVE USERS");
            }
        }
        return emailsOfCurrentUsers;

    }
}

