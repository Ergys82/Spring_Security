package com.bharath.springcloud.controllers;

import com.bharath.springcloud.model.Role;
import com.bharath.springcloud.model.User;
import com.bharath.springcloud.repos.UserRepo;
import com.bharath.springcloud.security.SecurityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashSet;

@Controller
public class UserController {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder encoder;

    @GetMapping("/showReg")
    public String showRegistrationPage() {
        return "registerUser";
    }

    @PostMapping("/registerUser")
    public String register(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        HashSet<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setId(2L);
        roles.add(role);
        user.setRoles(roles);
        userRepo.save(user);
        return "login";
    }



    @GetMapping("/")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(String email, String password, HttpServletRequest request, HttpServletResponse response) {

        boolean loginResponse = securityService.login(email, password, request, response);
        System.out.println(loginResponse);
        if(loginResponse) {
            return "index";
        } else {
            return "login";
        }

    }
}
