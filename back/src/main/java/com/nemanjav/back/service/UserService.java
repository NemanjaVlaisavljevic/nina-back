package com.nemanjav.back.service;


import com.nemanjav.back.dto.UserDto;
import com.nemanjav.back.entity.Cart;
import com.nemanjav.back.entity.ConfirmationToken;
import com.nemanjav.back.entity.User;
import com.nemanjav.back.enums.ResultEnum;
import com.nemanjav.back.exception.MyException;
import com.nemanjav.back.http.LoginRequest;
import com.nemanjav.back.http.LoginResponse;
import com.nemanjav.back.jwt.JwtProvider;
import com.nemanjav.back.repository.CartRepository;
import com.nemanjav.back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final ConfirmationTokenService confirmationTokenService;

    public User findOne(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if(existingUser.isPresent()){
            return existingUser.get();
        }else{
            throw new MyException(ResultEnum.USER_NOT_FOUND);
        }
    }

    @Transactional
    public ResponseEntity<User> saveUser(UserDto userDto){
        Optional<User> existingUser = userRepository.findByEmail(userDto.getEmail());
        if(existingUser.isPresent() && !existingUser.get().isEnabled()){
            // Registration already sent to that email
            return  ResponseEntity.badRequest().build();
        }
        if(existingUser.isPresent() && existingUser.get().getEmail().equals(userDto.getEmail()) && existingUser.get().isEnabled()){
            // User with email already exists
           return new ResponseEntity(HttpStatus.FORBIDDEN);
        }

        User newUser = getUserFromDto(userDto);
        try{
            User savedUser = userRepository.save(newUser);
            Cart newCart = cartRepository.save(new Cart(savedUser));
            savedUser.setCart(newCart);
            userRepository.saveAndFlush(savedUser);

            ConfirmationToken confirmationToken = confirmationTokenService.generateToken(savedUser);
            confirmationTokenService.saveConfirmationToken(confirmationToken);

            // Sending email
            confirmationTokenService.sendEmail(savedUser , confirmationToken.getToken());

            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);

        }catch(Exception e){
            throw new MyException(ResultEnum.VALID_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        try{

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername() , loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtProvider.generateToken(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByEmail(userDetails.getUsername()).get();

            return ResponseEntity.ok(new LoginResponse(jwt , user.getEmail() , user.getFirstName() , user.getUserRole().toString()));

        }catch(AuthenticationException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // TODO : Logout session
    public String logout(Authentication authentication){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getPrincipal().toString();
        if(auth.getPrincipal() != null){
            SecurityContextHolder.getContext().setAuthentication(null);
            return "Logged out : " + currentUser + " , currently logged in : " + SecurityContextHolder.getContext().getAuthentication();
        }else {
            return "No user logged in!";
        }
    }


    @Transactional
    public ResponseEntity<User> updateUser(UserDto userDto , Authentication authentication){
        if(userDto.getEmail() == null){
            return ResponseEntity.badRequest().build();
        }
        User oldUser = userRepository.findByEmail(userDto.getEmail()).orElseThrow(() -> new MyException(ResultEnum.USER_NOT_FOUND));
        if(oldUser.getEmail().equals(authentication.getPrincipal()) || authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            if (userDto.getFirstName() == null || userDto.getFirstName().isEmpty() || userDto.getFirstName().isBlank()) {
                oldUser.setFirstName(oldUser.getFirstName());
            } else {
                oldUser.setFirstName(userDto.getFirstName());
            }
            if (userDto.getLastName() == null || userDto.getLastName().isEmpty() || userDto.getLastName().isBlank()) {
                oldUser.setLastName(oldUser.getLastName());
            } else {
                oldUser.setLastName(userDto.getLastName());
            }
            if (userDto.getPassword() == null || userDto.getPassword().isEmpty() || userDto.getPassword().isBlank()) {
                oldUser.setPassword(oldUser.getPassword());
            } else {
                oldUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }
            if (userDto.getPhone() == null || userDto.getPhone().isEmpty() || userDto.getPhone().isBlank()) {
                oldUser.setPhone(oldUser.getPhone());
            } else {
                oldUser.setPhone(userDto.getPhone());
            }
            if (userDto.getStreetAndNumber() == null || userDto.getStreetAndNumber().isEmpty() || userDto.getStreetAndNumber().isBlank()) {
                oldUser.setStreetAndNumber(oldUser.getStreetAndNumber());
            } else {
                oldUser.setStreetAndNumber(userDto.getStreetAndNumber());
            }
            if (userDto.getCity() == null || userDto.getCity().isEmpty() || userDto.getCity().isBlank()) {
                oldUser.setCity(oldUser.getCity());
            } else {
                oldUser.setCity(userDto.getCity());
            }
        }else{
            return ResponseEntity.badRequest().build();
        }

        userRepository.saveAndFlush(oldUser);

        return new ResponseEntity<>(oldUser, HttpStatus.ACCEPTED);

    }

    public ResponseEntity<User> getCurrentUser(String email , Principal principal){
       if(principal.getName().equals(email)){
           return ResponseEntity.ok(findOne(email));
       }else{
           return ResponseEntity.badRequest().build();
       }
    }

    @Transactional
    private User findByEmailFromDatabase(String email){
        boolean exists = userRepository.findByEmail(email).isPresent();
        if(exists){
            return userRepository.findByEmail(email).get();
        }else{
            throw new MyException(ResultEnum.USER_NOT_FOUND);
        }
    }

    // Mapper from DTO to USER
    private User getUserFromDto(UserDto userDto){
        User newUser = new User();

        newUser.setCity(userDto.getCity());
        newUser.setStreetAndNumber(userDto.getStreetAndNumber());
        newUser.setEmail(userDto.getEmail());
        newUser.setFirstName(userDto.getFirstName());
        newUser.setLastName(userDto.getLastName());
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        newUser.setPhone(userDto.getPhone());
        newUser.setUserRole(userDto.getUserRole());
        newUser.setEnabled(false);
        newUser.setLocked(false);

        return newUser;
    }
    
}
