package com.example.saaapi.api.dto;

import com.example.saaapi.model.entity.Sala;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaDTO {

    private Long id;
    private String nome;
    private String descricao;
    private Long idDeposito;

    public static SalaDTO criar(Sala sala, Long idDeposito) {
        ModelMapper mapeador = new ModelMapper();
        SalaDTO dados = mapeador.map(sala, SalaDTO.class);
        dados.setIdDeposito(idDeposito);
        return dados;
    }
}
