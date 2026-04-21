package com.cenfotec.inkmapapi.models;

import com.cenfotec.inkmapapi.models.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Entidad que representa un usuario dentro del sistema.
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class User implements UserDetails {

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

    @Column(name = "bloqueado", nullable = false)
    private boolean blocked = false;

    @CreationTimestamp
    private LocalDateTime startDt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonIgnore
    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @JsonIgnore
    @Override
    @NonNull
    public String getUsername() {
        return email;
    }
  
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Project> projects;
}