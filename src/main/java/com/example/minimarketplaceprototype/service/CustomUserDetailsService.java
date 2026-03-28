package com.example.minimarketplaceprototype.service;

import com.example.minimarketplaceprototype.model.User;
import com.example.minimarketplaceprototype.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                true, // isEnabled
                true, // isAccountNonExpired
                true, // isCredentialsNonExpired
                !user.isBanned(), // isAccountNonLocked
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getName().name()))
        );
    }
}