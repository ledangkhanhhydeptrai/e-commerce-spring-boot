package com.example.demo.service.Implement;

import com.example.demo.Enum.UserRole;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.RegisterResponse;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.UsernameAlreadyExistsException;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.Interface.RegisterService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterServiceImpl(UserRepository userRepository,
                               RoleRepository roleRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerUser(RegisterRequest request) {
        String username = request.getUsername()
                .trim()
                .toLowerCase(); // 👈 CỰC KỲ QUAN TRỌNG
        System.out.println("Username: " + username);
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException();
        }

        Role role = roleRepository.findByName(UserRole.CUSTOMER)
                .orElseThrow(() -> new RuntimeException("ROLE_NOT_FOUND"));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        userRepository.save(user);
    }
}
