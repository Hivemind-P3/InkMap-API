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
public class ColorCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_codigoColor")
    private Long id;

    @ElementCollection
    @CollectionTable(
            name = "codigo_color_colores",
            joinColumns = @JoinColumn(name = "id_codigoColor")
    )
    @Column(name = "codigosHex")
    private List<String> colores = new ArrayList<>();
}
