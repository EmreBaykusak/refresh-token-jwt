package io.github.emrebaykusak.refreshtoken.services;

import jakarta.annotation.PostConstruct;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class InMemoryUserDetailsService implements UserDetailsService {
    private final Map<String, String> users = new HashMap<>();
    private final BCryptPasswordEncoder passwordEncoder;

    public InMemoryUserDetailsService(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        users.put("emrebaykusak", passwordEncoder.encode("123"));
    }

    @NullMarked
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (users.containsKey(username)) return new User(username, users.get(username), new ArrayList<>());

        throw new UsernameNotFoundException(username);
    }
}
