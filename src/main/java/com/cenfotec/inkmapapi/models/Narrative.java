package com.cenfotec.inkmapapi.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    private Project project;
}