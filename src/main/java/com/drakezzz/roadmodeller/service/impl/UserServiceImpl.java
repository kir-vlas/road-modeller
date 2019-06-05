package com.drakezzz.roadmodeller.service.impl;

import com.drakezzz.roadmodeller.persistence.entity.User;
import com.drakezzz.roadmodeller.persistence.repository.UserRepository;
import com.drakezzz.roadmodeller.service.UserService;
import com.drakezzz.roadmodeller.web.dto.StatusResult;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserService, ReactiveUserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public StatusResult registerUser(User user) {
        user.setCreatedAt(LocalDateTime.now());
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return StatusResult.ok();
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if(user == null) {
            return Mono.empty();
        }

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("user"));
        return Mono.just(new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities));
    }

}
