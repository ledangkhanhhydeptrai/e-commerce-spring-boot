package com.example.demo.service.Implement;

import com.example.demo.Enum.UserRole;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.EmailAlreadyExistsException;
import com.example.demo.exception.UsernameAlreadyExistsException;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.Interface.CloudinaryService;
import com.example.demo.service.Interface.RegisterService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    public RegisterServiceImpl(UserRepository userRepository,
                               RoleRepository roleRepository,
                               PasswordEncoder passwordEncoder,
                               CloudinaryService cloudinaryService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public void registerUser(RegisterRequest request, MultipartFile file) {
        String email = request.getEmail().trim().toLowerCase();
        String username = request.getUsername().trim().toLowerCase();
        System.out.println("Username: " + username);
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException();
        } else if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException();
        }

        Role role = roleRepository.findByName(UserRole.CUSTOMER)
                .orElseThrow(() -> new RuntimeException("ROLE_NOT_FOUND"));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        if (file != null && !file.isEmpty()) {
            try {
                String image = cloudinaryService.uploadFile(file);
                user.setImage(image);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Upload file thất bại");
            }
        }
        userRepository.save(user);
    }
}
