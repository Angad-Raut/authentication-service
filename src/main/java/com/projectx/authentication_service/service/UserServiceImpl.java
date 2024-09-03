package com.projectx.authentication_service.service;

import com.projectx.authentication_service.entity.Users;
import com.projectx.authentication_service.exceptions.AlreadyExistException;
import com.projectx.authentication_service.exceptions.ResourceNotFoundException;
import com.projectx.authentication_service.payloads.AuthResponse;
import com.projectx.authentication_service.payloads.EntityIdDto;
import com.projectx.authentication_service.payloads.LoginResponse;
import com.projectx.authentication_service.payloads.UserDto;
import com.projectx.authentication_service.repository.UserRepository;
import com.projectx.authentication_service.utils.UserConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

@Component
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Override
    public Users getUserByUserName(String username) {
        Users users = null;
        if (UserConstant.isMobile(username)) {
            users = userRepository.findByUserMobile(Long.parseLong(username));
        } else {
            users = userRepository.findByUserEmail(username);
        }
        return users;
    }

    @Override
    public Boolean addUser(UserDto dto, Boolean isAdmin) throws AlreadyExistException, ResourceNotFoundException {
        Users users = null;
        if (dto.getUserId()==null) {
            isMobileExist(dto.getUserMobile());
            isEmailExist(dto.getUserEmail());
            users = Users.builder()
                    .userName(dto.getUserName())
                    .userMobile(dto.getUserMobile())
                    .userEmail(dto.getUserEmail())
                    .userRole(isAdmin?UserConstant.ADMIN:UserConstant.USER)
                    .userStatus(true)
                    .insertedTime(new Date())
                    .updatedTime(new Date())
                    .userPassword(passwordEncoder.encode(dto.getUserPassword()))
                    .build();
        } else {
            users = userRepository.getUserById(dto.getUserId());
            if (users!=null) {
                if (!dto.getUserName().equals(users.getUserName())) {
                    users.setUserName(dto.getUserName());
                }
                if (!dto.getUserMobile().equals(users.getUserMobile())) {
                    isMobileExist(dto.getUserMobile());
                    users.setUserMobile(dto.getUserMobile());
                }
                if (!dto.getUserEmail().equals(users.getUserEmail())) {
                    users.setUserEmail(dto.getUserEmail());
                }
                users.setUpdatedTime(new Date());
            } else {
                throw new ResourceNotFoundException(UserConstant.USER_DETAILS_NOT_EXIST);
            }
        }
        try {
            return userRepository.save(users)!=null?true:false;
        } catch (AlreadyExistException e) {
            throw new AlreadyExistException(e.getMessage());
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    @Override
    public UserDto getById(EntityIdDto dto) throws ResourceNotFoundException {
        try {
            Users users = userRepository.getUserById(dto.getEntityId());
            if (users!=null) {
                return UserDto.builder()
                        .userId(users.getUserId())
                        .userName(users.getUserName())
                        .userMobile(users.getUserMobile())
                        .userEmail(users.getUserEmail())
                        .build();
            } else {
                throw new ResourceNotFoundException(UserConstant.USER_DETAILS_NOT_EXIST);
            }
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    @Override
    public Boolean isUserExist(Long mobile) {
        if (userRepository.existsByUserMobile(mobile)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public UserDto buildUser() {
        return UserDto.builder()
                .userName(UserConstant.NAME)
                .userMobile(UserConstant.MOBILE)
                .userEmail(UserConstant.EMAIL)
                .userPassword(UserConstant.PASSWORD)
                .build();
    }

    @Override
    public LoginResponse saveRefreshToken(String username) throws ParseException {
        return tokenService.getTokenDetails(username);
    }

    @Override
    public AuthResponse getRefreshToken(String username) throws ParseException {
        return tokenService.getRefreshToken(username);
    }

    private void isMobileExist(Long mobile) {
        if (userRepository.existsByUserMobile(mobile)) {
            throw new AlreadyExistException(UserConstant.MOBILE_NUMBER_ALREADY_EXIST);
        }
    }

    private void isEmailExist(String email) {
        if (userRepository.existsByUserEmail(email)) {
            throw new AlreadyExistException(UserConstant.EMAIL_ID_ALREADY_EXIST);
        }
    }
}
