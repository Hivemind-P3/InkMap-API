package com.cenfotec.inkmapapi.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "wikis")
@Getter
@Setter
public class Wiki {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wiki_id")
    private Long id;

    @Column(name = "titulo", nullable = false)
    private String title;

    @Column(name = "contenido", columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Project project;
}
