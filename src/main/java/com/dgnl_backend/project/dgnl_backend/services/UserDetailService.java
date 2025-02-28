package com.dgnl_backend.project.dgnl_backend.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.dgnl_backend.project.dgnl_backend.repositories.UserRepository;
import com.dgnl_backend.project.dgnl_backend.schemas.identity.Role;
import com.dgnl_backend.project.dgnl_backend.schemas.identity.User;

/**
 * Service class for loading user details from the database.
 * Implements {@link UserDetailsService} to provide authentication details.
 */
@Component
public class UserDetailService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository; // Repository for user-related database operations

    /**
     * Loads user details by user ID.
     *
     * @param id the UUID of the user
     * @return UserDetails containing user authentication details
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        
        // Convert the string ID to a UUID and attempt to find the user in the database
        Optional<User> user = userRepository.findById(UUID.fromString(id));
        
        if (user.isPresent()) {
            // Retrieve the role associated with the user
            Role role = user.get().getRole();
            
            // If the role is not found, throw an exception
            // if (!role.isPresent()) throw new RoleNotFoundException("Role not found with id: " + user.get().getRoleId());
            
            // Build and return the UserDetails object using Spring Security's User class
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.get().getUsername()) // Set the username
                    .password(user.get().getPassword()) // Set the password
                    .roles(role.getRoleName().toUpperCase()) // Assign roles to the user
                    .build();
        } else {
            // If user is not found, throw an exception
            throw new UsernameNotFoundException("User not found with id: " + id);
        }
    }
}