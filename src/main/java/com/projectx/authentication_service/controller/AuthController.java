package com.projectx.authentication_service.controller;

import com.projectx.authentication_service.exceptions.AlreadyExistException;
import com.projectx.authentication_service.exceptions.InvalidUserException;
import com.projectx.authentication_service.exceptions.ResourceNotFoundException;
import com.projectx.authentication_service.exceptions.TokenExpiryedException;
import com.projectx.authentication_service.payloads.*;
import com.projectx.authentication_service.service.JwtService;
import com.projectx.authentication_service.service.UserService;
import com.projectx.authentication_service.utils.ErrorHandlerComponent;
import com.projectx.authentication_service.utils.UserConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private ErrorHandlerComponent errorHandler;

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<LoginResponse>> authenticate(
            @Valid @RequestBody AuthRequest requestDto, BindingResult result) {
        if (result.hasErrors()) {
            return errorHandler.handleValidationErrors(result);
        }
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            requestDto.getUsername(), requestDto.getPassword()));
            if (authentication.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                LoginResponse response = userService.saveRefreshToken(requestDto.getUsername());
                return new ResponseEntity<ResponseDto<LoginResponse>>(
                        new ResponseDto<LoginResponse>(response,null), HttpStatus.OK);
            } else {
                throw new InvalidUserException(UserConstant.INVALID_USER);
            }
        } catch (TokenExpiryedException | InvalidUserException e) {
            return errorHandler.handleError(e);
        }  catch (Exception e) {
            return new ResponseEntity<>(new ResponseDto<>(null,e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<ResponseDto<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest requestDto, BindingResult result) {
        if (result.hasErrors()) {
            return errorHandler.handleValidationErrors(result);
        }
        try {
            AuthResponse response = userService.getRefreshToken(requestDto.getUserName());
            return new ResponseEntity<ResponseDto<AuthResponse>>(
                    new ResponseDto<AuthResponse>(response,null), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDto<>(
                    null,e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public ResponseEntity<ResponseDto<Boolean>> logoutPage (
            HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Boolean result = false;
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
            result = true;
        }
        return new ResponseEntity<ResponseDto<Boolean>>(
                new ResponseDto<Boolean>(result,""), HttpStatus.OK);
    }

    @PostMapping("/registerUser")
    public ResponseEntity<ResponseDto<Boolean>> registerUser(
            @Valid @RequestBody UserDto dto, BindingResult result) {
        if (result.hasErrors()) {
            return errorHandler.handleValidationErrors(result);
        }
        try {
            Boolean data = userService.addUser(dto,false);
            return new ResponseEntity<ResponseDto<Boolean>>(
                    new ResponseDto<Boolean>(data,null), HttpStatus.CREATED);
        } catch (ResourceNotFoundException | AlreadyExistException e) {
            return errorHandler.handleError(e);
        }
    }

    @PostMapping("/getUserDetailsById")
    public ResponseEntity<ResponseDto<UserDto>> getUserDetailsById(
            @Valid @RequestBody EntityIdDto dto, BindingResult result) {
        if (result.hasErrors()) {
            return errorHandler.handleValidationErrors(result);
        }
        try {
            UserDto data = userService.getById(dto);
            return new ResponseEntity<ResponseDto<UserDto>>(
                    new ResponseDto<UserDto>(data,null), HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return errorHandler.handleError(e);
        }
    }
}
