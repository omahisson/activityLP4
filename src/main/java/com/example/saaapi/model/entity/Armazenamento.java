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
public class Armazenamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dataEntrada;

    private Float quantidade;

    private String observacao;

    @ManyToOne
    private MaterialRadioativo materialRadioativo;

    @ManyToOne
    private Recipiente recipiente;

    @ManyToOne
    private Responsavel responsavel;

    public void validarDados() {
        if (getMaterialRadioativo() == null) { //1
            throw new RegraNegocioException("Material radioativo não informado");
        }
        if (getMaterialRadioativo().getId() == null) { //2
            throw new RegraNegocioException("Material radioativo inválido");
        }
        if (getRecipiente() == null) { //3
            throw new RegraNegocioException("Recipiente não informado");
        }
        if (getRecipiente().getId() == null) { //4
            throw new RegraNegocioException("Recipiente inválido");
        }
        if (getResponsavel() == null) { //5
            throw new RegraNegocioException("Responsável não informado");
        }
        if (getResponsavel().getId() == null) { //6
            throw new RegraNegocioException("Responsável inválido");
        }
        if (getDataEntrada() == null) { //7
            throw new RegraNegocioException("Data de entrada não informada");
        }
        if (getDataEntrada().trim().length() != 10) { //8
            throw new RegraNegocioException("Data de entrada inválida");
        }
        if (getQuantidade() == null) { //9
            throw new RegraNegocioException("Quantidade não informada");
        }
        if (getQuantidade() <= 0) { //10
            throw new RegraNegocioException("Quantidade inválida");
        }
        if (getRecipiente().getId() < 1) { //11
            throw new RegraNegocioException("Recipiente inválido");
        }
        if (getObservacao() != null && getObservacao().trim().length() > 200) { //12
            throw new RegraNegocioException("Observação excede 200 caracteres");
        }
    } //13
}
