package com.cenfotec.inkmapapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NarrativeAssociationsResponseDTO {
    private List<NarrativeCharacterDTO> characters;
    private List<NarrativeWikiDTO> places;
}