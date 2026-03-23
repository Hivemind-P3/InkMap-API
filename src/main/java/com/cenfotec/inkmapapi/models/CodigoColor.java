package com.cenfotec.inkmapapi.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


/**
 * Entidad que representa el código de color de preferencia del usuario
 */
@Entity
@Table(name = "codigoColor")
@Getter
@Setter
public class CodigoColor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_codigoColor")
    private Long id;

    @Column(nullable = false)
    private List<String> colores = new ArrayList<>();
}
