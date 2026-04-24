package com.cenfotec.inkmapapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeographicMapSaveDTO {
    private String konvaJson;
    private String name;
    private String thumbnail;
}
