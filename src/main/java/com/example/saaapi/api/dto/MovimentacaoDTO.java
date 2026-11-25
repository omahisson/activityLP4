package com.example.saaapi.api.dto;

import com.example.saaapi.model.entity.Movimentacao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimentacaoDTO {

    private Long id;
    private String tipo;
    private String dataMovimentacao;
    private Float quantidade;
    private String observacao;
    private Long idArmazenamento;
    private Long idResponsavel;

    public static MovimentacaoDTO criar(Movimentacao movimentacao) {
        ModelMapper mapeador = new ModelMapper();
        MovimentacaoDTO dados = mapeador.map(movimentacao, MovimentacaoDTO.class);
        dados.setIdArmazenamento(movimentacao.getArmazenamento().getId());
        dados.setIdResponsavel(movimentacao.getResponsavel().getId());
        return dados;
    }
}
