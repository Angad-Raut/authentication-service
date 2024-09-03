package com.projectx.authentication_service.service;

import com.projectx.authentication_service.entity.Users;
import com.projectx.authentication_service.exceptions.AlreadyExistException;
import com.projectx.authentication_service.exceptions.ResourceNotFoundException;
import com.projectx.authentication_service.payloads.AuthResponse;
import com.projectx.authentication_service.payloads.EntityIdDto;
import com.projectx.authentication_service.payloads.LoginResponse;
import com.projectx.authentication_service.payloads.UserDto;

import java.text.ParseException;

public interface UserService {
    Users getUserByUserName(String username);
    Boolean addUser(UserDto dto, Boolean isAdmin)throws AlreadyExistException, ResourceNotFoundException;
    UserDto getById(EntityIdDto dto)throws ResourceNotFoundException;
    Boolean isUserExist(Long mobile);
    UserDto buildUser();
    LoginResponse saveRefreshToken(String username) throws ParseException;
    AuthResponse getRefreshToken(String username) throws ParseException;
}
