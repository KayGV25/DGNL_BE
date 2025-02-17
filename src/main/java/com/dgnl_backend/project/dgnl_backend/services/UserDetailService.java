package com.dgnl_backend.project.dgnl_backend.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.dgnl_backend.project.dgnl_backend.repositories.UserRepository;
import com.dgnl_backend.project.dgnl_backend.schemas.User;

@Component
public class UserDetailService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findById(UUID.fromString(id));
        if (user.isPresent()) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.get().getUsername())
                    .password(user.get().getPassword())
                    .roles(user.get().getRoleId() == 1 ? "ADMIN" : "USER")
                    .build();
        } else {
            throw new UsernameNotFoundException("User not found with id: " + id);
        }
    }
}
