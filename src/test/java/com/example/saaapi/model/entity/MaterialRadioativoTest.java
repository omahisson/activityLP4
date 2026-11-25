package com.example.saaapi.model.entity;

import com.example.saaapi.exception.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class MaterialRadioativoTest {

    private MaterialRadioativo material;

    @BeforeEach
    public void prepararMaterialValido() {
        TipoMaterial tipo = new TipoMaterial();
        tipo.setId(1L);

        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setId(1L);

        material = new MaterialRadioativo();
        material.setNome("Ar");
        material.setCodigo("M12");
        material.setQuantidade(0.01f);
        material.setTipoMaterial(tipo);
        material.setFornecedor(fornecedor);
    }

    @Test
    public void deveRejeitarNomeNulo() {
        material.setNome(null);
        verificarRejeicao("Nome não informado");
    }

    @Test
    public void deveRejeitarNomeComUmCaractere() {
        material.setNome("A");
        verificarRejeicao("Nome inválido");
    }

    @Test
    public void deveRejeitarCodigoNulo() {
        material.setCodigo(null);
        verificarRejeicao("Código não informado");
    }

    @Test
    public void deveRejeitarCodigoComDoisCaracteres() {
        material.setCodigo("M1");
        verificarRejeicao("Código inválido");
    }

    @Test
    public void deveRejeitarQuantidadeNula() {
        material.setQuantidade(null);
        verificarRejeicao("Quantidade não informada");
    }

    @Test
    public void deveRejeitarQuantidadeZero() {
        material.setQuantidade(0f);
        verificarRejeicao("Quantidade inválida");
    }

    @Test
    public void deveRejeitarTipoDeMaterialNulo() {
        material.setTipoMaterial(null);
        verificarRejeicao("Tipo de material não informado");
    }

    @Test
    public void deveRejeitarTipoDeMaterialSemId() {
        material.getTipoMaterial().setId(null);
        verificarRejeicao("Tipo de material inválido");
    }

    @Test
    public void deveRejeitarFornecedorNulo() {
        material.setFornecedor(null);
        verificarRejeicao("Fornecedor não informado");
    }

    @Test
    public void deveRejeitarFornecedorSemId() {
        material.getFornecedor().setId(null);
        verificarRejeicao("Fornecedor inválido");
    }

    @Test
    public void m11_deveAceitarMaterialComNomeECodigoNosLimitesMinimos() {
        material.validarDados();
    }

    private void verificarRejeicao(String mensagemEsperada) {
        try {
            material.validarDados();
            fail("O método deveria ter rejeitado o material radioativo.");
        } catch (RegraNegocioException e) {
            assertEquals(mensagemEsperada, e.getMessage());
        }
    }
}