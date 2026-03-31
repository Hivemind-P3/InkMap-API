package com.cenfotec.inkmapapi.models;

import com.cenfotec.inkmapapi.models.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "personajes")
@Getter
@Setter
public class StoryCharacter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "personaje_id")
    private Long id;

    @Column(name = "nombre")
    private String name;

    @Column(name = "rol")
    private String role;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String description;

    @Column(name = "edad")
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(name = "genero")
    private Gender gender;

    @Column(name = "raza")
    private String race;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Project project;
}