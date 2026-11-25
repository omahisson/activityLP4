package com.example.saaapi.api.dto;

import com.example.saaapi.model.entity.Fornecedor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FornecedorDTO {

    private Long id;
    private String nome;
    private String cnpj;
    private String email;
    private String telefone;

    public static FornecedorDTO criar(Fornecedor fornecedor) {
        ModelMapper mapeador = new ModelMapper();
        FornecedorDTO dados = mapeador.map(fornecedor, FornecedorDTO.class);
        return dados;
    }
}
