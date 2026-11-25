package com.example.saaapi.api.dto;

import com.example.saaapi.model.entity.Responsavel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponsavelDTO {

    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String celular;
    private String login;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senha;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senhaRepeticao;

    public static ResponsavelDTO criar(Responsavel responsavel) {
        ModelMapper mapeador = new ModelMapper();
        ResponsavelDTO dados = mapeador.map(responsavel, ResponsavelDTO.class);
        return dados;
    }
}
