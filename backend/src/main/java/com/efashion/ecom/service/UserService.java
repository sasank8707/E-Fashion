package com.efashion.ecom.service;

import com.efashion.ecom.entity.User;
import com.efashion.ecom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public User registerUser(User user) {
        // Basic Validations
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        // Hash password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER"); // Default role

        User savedUser = userRepository.save(user);
        savedUser.setPassword(null); // Clear password for security
        return savedUser;
    }

    public User loginUser(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Email and password are required");
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Return user without password
        User responseUser = new User();
        responseUser.setId(user.getId());
        responseUser.setName(user.getName());
        responseUser.setEmail(user.getEmail());
        responseUser.setRole(user.getRole());
        return responseUser;
    }

    public User getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setPassword(null);
        return user;
    }
}
