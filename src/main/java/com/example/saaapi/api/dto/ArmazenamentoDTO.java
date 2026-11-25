package com.example.saaapi.api.dto;

import com.example.saaapi.model.entity.Armazenamento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArmazenamentoDTO {

    private Long id;
    private String dataEntrada;
    private Float quantidade;
    private String observacao;
    private Long idMaterialRadioativo;
    private Long idRecipiente;
    private Long idResponsavel;

    public static ArmazenamentoDTO criar(Armazenamento armazenamento) {
        ModelMapper mapeador = new ModelMapper();
        ArmazenamentoDTO dados = mapeador.map(armazenamento, ArmazenamentoDTO.class);
        dados.setIdMaterialRadioativo(armazenamento.getMaterialRadioativo().getId());
        dados.setIdRecipiente(armazenamento.getRecipiente().getId());
        dados.setIdResponsavel(armazenamento.getResponsavel().getId());
        return dados;
    }
}
