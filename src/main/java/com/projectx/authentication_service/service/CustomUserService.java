package com.projectx.authentication_service.service;

import com.projectx.authentication_service.entity.CustomUserDetails;
import com.projectx.authentication_service.entity.Users;
import com.projectx.authentication_service.repository.UserRepository;
import com.projectx.authentication_service.utils.UserConstant;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class CustomUserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = null;
        if (UserConstant.isMobile(username)) {
            users = userRepository.findByUserMobile(Long.parseLong(username));
        } else {
            users = userRepository.findByUserEmail(username);
        }

        if(users==null)
            throw new UsernameNotFoundException("User with email: " +username +" not found !");
        else {
            return new CustomUserDetails(users);
        }
    }
}
