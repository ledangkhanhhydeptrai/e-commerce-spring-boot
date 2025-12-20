package com.example.demo.config;

import com.example.demo.Enum.UserRole;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {

        // ✅ Tạo role
        Role adminRole = createRole(roleRepository, UserRole.ADMIN);
        Role customerRole = createRole(roleRepository, UserRole.CUSTOMER);

        // ✅ Tạo admin account
        if (!userRepository.existsByUsername("admin")) {
            User adminUser = User.builder()
                    .username("admin")
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("123456"))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .role(adminRole)
                    .build();

            userRepository.save(adminUser);
        }
    }

    private Role createRole(RoleRepository repo, UserRole name) {
        return repo.findByName(name)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(name);
                    return repo.save(role);
                });
    }
}
