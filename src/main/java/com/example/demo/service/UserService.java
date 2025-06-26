package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User validateUser(String name, String password) {
        try {
            User user = userRepository.findByNameAndPassword(name, password);
            System.out.println("Login Attempt: name=" + name + ", password=" + password);
            System.out.println("Result: " + (user != null ? "Success" : "Failed"));
            return user;
        } catch (NumberFormatException e) {
            return null;
        }
    }

}