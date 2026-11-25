package com.example.saaapi.api.dto;

import com.example.saaapi.model.entity.MaterialRadioativo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialRadioativoDTO {

    private Long id;
    private String codigo;
    private String nome;
    private Float quantidade;
    private String unidade;
    private String dataCadastro;
    private Long idTipoMaterial;
    private Long idFornecedor;

    public static MaterialRadioativoDTO criar(MaterialRadioativo materialRadioativo) {
        ModelMapper mapeador = new ModelMapper();
        MaterialRadioativoDTO dados = mapeador.map(materialRadioativo, MaterialRadioativoDTO.class);
        dados.setIdTipoMaterial(materialRadioativo.getTipoMaterial().getId());
        dados.setIdFornecedor(materialRadioativo.getFornecedor().getId());
        return dados;
    }
}
