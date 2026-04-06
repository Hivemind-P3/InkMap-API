package com.cenfotec.inkmapapi.models;

import com.cenfotec.inkmapapi.models.enums.NodeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "nodos")
@Getter
@Setter
public class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nodo_id")
    private Long id;

    @Column(name = "etiqueta")
    private String label;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private NodeType type;

    @Column(name = "color")
    private String color;

    @Column(name = "pos_x")
    private Double posX;

    @Column(name = "pos_y")
    private Double posY;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mapa_nodos_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private NodeMap nodeMap;
}
