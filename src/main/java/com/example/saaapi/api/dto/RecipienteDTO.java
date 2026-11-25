package com.example.saaapi.api.dto;

import com.example.saaapi.model.entity.Recipiente;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipienteDTO {

    private Long id;
    private String codigo;
    private String descricao;
    private Long idSala;

    public static RecipienteDTO criar(Recipiente recipiente) {
        ModelMapper mapeador = new ModelMapper();
        RecipienteDTO dados = mapeador.map(recipiente, RecipienteDTO.class);
        dados.setIdSala(recipiente.getSala().getId());
        return dados;
    }
}
