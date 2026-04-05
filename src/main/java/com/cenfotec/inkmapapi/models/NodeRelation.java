package com.cenfotec.inkmapapi.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "relaciones_nodos")
@Getter
@Setter
public class NodeRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "relacion_id")
    private Long id;

    @Column(name = "etiqueta")
    private String label;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nodo_origen_id")
    private Node sourceNode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nodo_destino_id")
    private Node targetNode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mapa_nodos_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private NodeMap nodeMap;
}
