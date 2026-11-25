package com.example.saaapi.api.dto;

import com.example.saaapi.model.entity.TipoMaterial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoMaterialDTO {

    private Long id;
    private String nome;
    private String descricao;

    public static TipoMaterialDTO criar(TipoMaterial tipoMaterial) {
        ModelMapper mapeador = new ModelMapper();
        TipoMaterialDTO dados = mapeador.map(tipoMaterial, TipoMaterialDTO.class);
        return dados;
    }
}
