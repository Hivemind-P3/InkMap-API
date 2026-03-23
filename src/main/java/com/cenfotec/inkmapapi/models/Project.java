package com.cenfotec.inkmapapi.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "proyectos")
@Getter
@Setter
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proyecto_id")
    private Long id;

    @Column(name = "titulo")
    private String title;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String description;

    @Column(name = "medio")
    private String medium;

    @ElementCollection
    @CollectionTable(name = "proyecto_tags", joinColumns = @JoinColumn(name = "proyecto_id"))
    @Column(name = "tag", columnDefinition = "TEXT")
    private List<String> tags;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private User user;
}
