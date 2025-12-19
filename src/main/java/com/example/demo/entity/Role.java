package com.example.demo.entity;

import com.example.demo.Enum.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@Table(name = "roles")
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue
    private UUID id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private UserRole name;
    @OneToMany(mappedBy = "role")
    private Set<User> users;
}
