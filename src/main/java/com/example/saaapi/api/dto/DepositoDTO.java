package com.example.saaapi.api.dto;

import com.example.saaapi.model.entity.Deposito;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositoDTO {

    private Long id;
    private String nome;
    private String endereco;
    private String descricao;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<SalaDTO> salas = new ArrayList<>();

    public static DepositoDTO criar(Deposito deposito) {
        List<SalaDTO> salas = deposito.getSalas().stream()
                .map(sala -> SalaDTO.criar(sala, deposito.getId())).collect(Collectors.toList());
        return new DepositoDTO(deposito.getId(), deposito.getNome(), deposito.getEndereco(),
                deposito.getDescricao(), salas);
    }
}
