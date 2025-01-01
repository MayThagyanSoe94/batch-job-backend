package com.maysoft.batch.job.controller;

import com.maysoft.batch.job.entity.MyUser;
import com.maysoft.batch.job.repository.MyUserRepository;
import com.maysoft.batch.job.service.MyUserDetailService;
import com.maysoft.batch.job.webtoken.JwtService;
import com.maysoft.batch.job.webtoken.LoginForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {

    @Autowired
    private MyUserRepository myUserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailService myUserDetailService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register/user")
    public MyUser createUser(@RequestBody MyUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return myUserRepository.save(user);
    }

    @PostMapping("/authenticate")
    public String authenticateAndGetToken(@RequestBody LoginForm loginForm) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginForm.getUsername(), loginForm.getPassword()
        ));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(myUserDetailService.loadUserByUsername(loginForm.getUsername()));
        } else {
            throw new UsernameNotFoundException("Invalid credentials");
        }
    }
}
