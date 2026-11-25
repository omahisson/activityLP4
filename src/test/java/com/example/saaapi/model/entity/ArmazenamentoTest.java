package com.example.saaapi.model.entity;

import com.example.saaapi.exception.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ArmazenamentoTest {

    private Armazenamento armazenamento;

    @BeforeEach
    public void prepararArmazenamentoValido() {
        MaterialRadioativo material = new MaterialRadioativo();
        material.setId(1L);

        Recipiente recipiente = new Recipiente();
        recipiente.setId(1L);

        Responsavel responsavel = new Responsavel();
        responsavel.setId(1L);

        armazenamento = new Armazenamento();
        armazenamento.setMaterialRadioativo(material);
        armazenamento.setRecipiente(recipiente);
        armazenamento.setResponsavel(responsavel);
        armazenamento.setDataEntrada("2026-11-24");
        armazenamento.setQuantidade(0.01f);
        armazenamento.setObservacao("A".repeat(200));
    }

    @Test
    public void deveRejeitarMaterialRadioativoNulo() {
        armazenamento.setMaterialRadioativo(null);
        verificarRejeicao("Material radioativo não informado");
    }

    @Test
    public void deveRejeitarMaterialRadioativoSemId() {
        armazenamento.getMaterialRadioativo().setId(null);
        verificarRejeicao("Material radioativo inválido");
    }

    @Test
    public void deveRejeitarRecipienteNulo() {
        armazenamento.setRecipiente(null);
        verificarRejeicao("Recipiente não informado");
    }

    @Test
    public void deveRejeitarRecipienteSemId() {
        armazenamento.getRecipiente().setId(null);
        verificarRejeicao("Recipiente inválido");
    }

    @Test
    public void deveRejeitarResponsavelNulo() {
        armazenamento.setResponsavel(null);
        verificarRejeicao("Responsável não informado");
    }

    @Test
    public void deveRejeitarResponsavelSemId() {
        armazenamento.getResponsavel().setId(null);
        verificarRejeicao("Responsável inválido");
    }

    @Test
    public void deveRejeitarDataDeEntradaNula() {
        armazenamento.setDataEntrada(null);
        verificarRejeicao("Data de entrada não informada");
    }

    @Test
    public void deveRejeitarDataDeEntradaComNoveCaracteres() {
        armazenamento.setDataEntrada("2026-11-2");
        verificarRejeicao("Data de entrada inválida");
    }

    @Test
    public void deveRejeitarQuantidadeNula() {
        armazenamento.setQuantidade(null);
        verificarRejeicao("Quantidade não informada");
    }

    @Test
    public void deveRejeitarQuantidadeZero() {
        armazenamento.setQuantidade(0f);
        verificarRejeicao("Quantidade inválida");
    }

    @Test
    public void deveRejeitarRecipienteComIdZero() {
        armazenamento.getRecipiente().setId(0L);
        verificarRejeicao("Recipiente inválido");
    }

    @Test
    public void deveRejeitarObservacaoCom201Caracteres() {
        armazenamento.setObservacao("A".repeat(201));
        verificarRejeicao("Observação excede 200 caracteres");
    }

    @Test
    public void deveAceitarObservacaoNula() {
        armazenamento.setObservacao(null);
        armazenamento.validarDados();
    }

    @Test
    public void deveAceitarArmazenamentoValidoComObservacaoDe200Caracteres() {
        armazenamento.setObservacao("A".repeat(200));
        armazenamento.validarDados();
    }

    private void verificarRejeicao(String mensagemEsperada) {
        try {
            armazenamento.validarDados();
            fail("O método deveria ter rejeitado o armazenamento.");
        } catch (RegraNegocioException e) {
            assertEquals(mensagemEsperada, e.getMessage());
        }
    }
}