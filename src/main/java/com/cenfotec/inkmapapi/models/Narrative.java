package com.cenfotec.inkmapapi.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "contenido_narrativo")
@Getter
@Setter
public class Narrative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contenido_id")
    private Long id;

    @Column(name = "titulo", nullable = false, length = 120)
    private String title;

    @Lob
    @Column(name = "contenido", nullable = false)
    private String content; // JSON tipo Delta (Quill)

    @Column(name = "orden", nullable = false)
    private Integer order;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime updateTime;

    @ManyToOne
    @JoinColumn(name = "id_proyecto", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Project project;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "contenido_personajes",
            joinColumns = @JoinColumn(name = "contenido_id"),
            inverseJoinColumns = @JoinColumn(name = "personaje_id"))
    private Set<StoryCharacter> characters = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "contenido_wikis",
            joinColumns = @JoinColumn(name = "contenido_id"),
            inverseJoinColumns = @JoinColumn(name = "wiki_id"))
    private Set<Wiki> places = new HashSet<>();
}