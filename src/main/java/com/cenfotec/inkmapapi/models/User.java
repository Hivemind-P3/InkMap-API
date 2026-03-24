package com.cenfotec.inkmapapi.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad que representa un usuario dentro del sistema.
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Long id;

    @Column(name = "correo", unique = true)
    private String email;

    @Column(name = "nombre")
    private String name;

    @Column(name = "proveedor")
    private String provider;

    @Column(name = "contrasena")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol")
    private Role role;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime startDt;
}