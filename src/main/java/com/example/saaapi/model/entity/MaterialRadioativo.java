package com.example.saaapi.model.entity;

import com.example.saaapi.exception.RegraNegocioException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialRadioativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;

    private String nome;

    private Float quantidade;

    private String unidade;

    private String dataCadastro;

    @ManyToOne
    private TipoMaterial tipoMaterial;

    @ManyToOne
    private Fornecedor fornecedor;

    public void validarDados() {
        if (getNome() == null) { //1
            throw new RegraNegocioException("Nome não informado");
        }
        if (getNome().trim().length() < 2) { //2
            throw new RegraNegocioException("Nome inválido");
        }
        if (getCodigo() == null) { //3
            throw new RegraNegocioException("Código não informado");
        }
        if (getCodigo().trim().length() < 3) { //4
            throw new RegraNegocioException("Código inválido");
        }
        if (getQuantidade() == null) { //5
            throw new RegraNegocioException("Quantidade não informada");
        }
        if (getQuantidade() <= 0) { //6
            throw new RegraNegocioException("Quantidade inválida");
        }
        if (getTipoMaterial() == null) { //7
            throw new RegraNegocioException("Tipo de material não informado");
        }
        if (getTipoMaterial().getId() == null) { //8
            throw new RegraNegocioException("Tipo de material inválido");
        }
        if (getFornecedor() == null) { //9
            throw new RegraNegocioException("Fornecedor não informado");
        }
        if (getFornecedor().getId() == null) { //10
            throw new RegraNegocioException("Fornecedor inválido");
        }
    } //11
}
