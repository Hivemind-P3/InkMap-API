package com.cenfotec.inkmapapi.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "puntosDeInteres")
@Getter
@Setter
public class PointOfInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "puntoDeInteres_id")
    private Long id;

    @Column(name = "pos_x")
    private Double posX;

    @Column(name = "pos_y")
    private Double posY;

    @ManyToOne
    @JoinColumn(name = "mapaGeografico_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private GeographicMap geographicMap;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "puntoDeInteres_wikis",
            joinColumns = @JoinColumn(name = "puntoDeInteres_id"),
            inverseJoinColumns = @JoinColumn(name = "wiki_id")
    )
    private Set<Wiki> wikis = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
