package com.cenfotec.inkmapapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entiddad que representa las preferencias del usuario
 */
@Entity
@Table(name = "preferencias")
@Getter
@Setter
public class Preferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_preferencias")
    private Long id;

    private boolean notificacionesCorreo;

    @CreationTimestamp
    private LocalDateTime createAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToOne
    @JoinColumn(name = "id_codigoColor")
    private ColorCode colorCode;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "usuario_id")
    private User user;
}
