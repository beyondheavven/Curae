package com.vention.medfast.entity;


import com.vention.medfast.utils.UserRole;
import jakarta.persistence.*;


@Table(name = "users")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "profile_url")
    private String profilePictureUrl;

    @Enumerated(EnumType.STRING)
    private UserRole role;
}
