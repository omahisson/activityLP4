package com.example.saaapi.api.dto;

import com.example.saaapi.model.entity.Usuario;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    private Long id;
    private String login;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senha;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senhaRepeticao;
    private String cpf;
    private boolean admin;

    public static UsuarioDTO criar(Usuario usuario) {
        ModelMapper mapeador = new ModelMapper();
        UsuarioDTO dados = mapeador.map(usuario, UsuarioDTO.class);
        return dados;
    }
}
