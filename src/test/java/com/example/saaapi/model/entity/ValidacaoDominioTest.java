package com.example.saaapi.model.entity;

import com.example.saaapi.exception.RegraNegocioException;
import com.example.saaapi.model.repository.ArmazenamentoRepository;
import com.example.saaapi.model.repository.MaterialRadioativoRepository;
import com.example.saaapi.service.ArmazenamentoService;
import com.example.saaapi.service.MaterialRadioativoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ValidacaoDominioTest {

    @ParameterizedTest
    @MethodSource("armazenamentosInvalidos")
    void rejeitarArmazenamentoInvalido(Consumer<Armazenamento> alterar, String mensagem) {
        Armazenamento armazenamento = criarArmazenamento();
        alterar.accept(armazenamento);
        ArmazenamentoRepository repositorio = mock(ArmazenamentoRepository.class);
        ArmazenamentoService servico = new ArmazenamentoService(repositorio);

        RegraNegocioException excecao = assertThrows(RegraNegocioException.class,
                () -> servico.salvar(armazenamento));

        assertEquals(mensagem, excecao.getMessage());
        verifyNoInteractions(repositorio);
    }

    static Stream<Arguments> armazenamentosInvalidos() {
        return Stream.of(
                Arguments.of((Consumer<Armazenamento>) registro -> registro.setMaterialRadioativo(null), "Material radioativo não informado"),
                Arguments.of((Consumer<Armazenamento>) registro -> registro.getMaterialRadioativo().setId(null), "Material radioativo inválido"),
                Arguments.of((Consumer<Armazenamento>) registro -> registro.setRecipiente(null), "Recipiente não informado"),
                Arguments.of((Consumer<Armazenamento>) registro -> registro.getRecipiente().setId(null), "Recipiente inválido"),
                Arguments.of((Consumer<Armazenamento>) registro -> registro.setResponsavel(null), "Responsável não informado"),
                Arguments.of((Consumer<Armazenamento>) registro -> registro.getResponsavel().setId(null), "Responsável inválido"),
                Arguments.of((Consumer<Armazenamento>) registro -> registro.setDataEntrada(null), "Data de entrada não informada"),
                Arguments.of((Consumer<Armazenamento>) registro -> registro.setDataEntrada("  "), "Data de entrada inválida"),
                Arguments.of((Consumer<Armazenamento>) registro -> registro.setQuantidade(null), "Quantidade não informada"),
                Arguments.of((Consumer<Armazenamento>) registro -> registro.setQuantidade(0f), "Quantidade inválida"),
                Arguments.of((Consumer<Armazenamento>) registro -> registro.setQuantidade(-1f), "Quantidade inválida")
        );
    }

    @ParameterizedTest
    @MethodSource("materiaisInvalidos")
    void rejeitarMaterialInvalido(Consumer<MaterialRadioativo> alterar, String mensagem) {
        MaterialRadioativo material = criarMaterial();
        alterar.accept(material);
        MaterialRadioativoRepository repositorio = mock(MaterialRadioativoRepository.class);
        MaterialRadioativoService servico = new MaterialRadioativoService(repositorio);

        RegraNegocioException excecao = assertThrows(RegraNegocioException.class,
                () -> servico.salvar(material));

        assertEquals(mensagem, excecao.getMessage());
        verifyNoInteractions(repositorio);
    }

    static Stream<Arguments> materiaisInvalidos() {
        return Stream.of(
                Arguments.of((Consumer<MaterialRadioativo>) registro -> registro.setNome(null), "Nome não informado"),
                Arguments.of((Consumer<MaterialRadioativo>) registro -> registro.setNome("  "), "Nome inválido"),
                Arguments.of((Consumer<MaterialRadioativo>) registro -> registro.setCodigo(null), "Código não informado"),
                Arguments.of((Consumer<MaterialRadioativo>) registro -> registro.setCodigo("  "), "Código inválido"),
                Arguments.of((Consumer<MaterialRadioativo>) registro -> registro.setQuantidade(null), "Quantidade não informada"),
                Arguments.of((Consumer<MaterialRadioativo>) registro -> registro.setQuantidade(0f), "Quantidade inválida"),
                Arguments.of((Consumer<MaterialRadioativo>) registro -> registro.setQuantidade(-1f), "Quantidade inválida"),
                Arguments.of((Consumer<MaterialRadioativo>) registro -> registro.setTipoMaterial(null), "Tipo de material não informado"),
                Arguments.of((Consumer<MaterialRadioativo>) registro -> registro.getTipoMaterial().setId(null), "Tipo de material inválido"),
                Arguments.of((Consumer<MaterialRadioativo>) registro -> registro.setFornecedor(null), "Fornecedor não informado"),
                Arguments.of((Consumer<MaterialRadioativo>) registro -> registro.getFornecedor().setId(null), "Fornecedor inválido")
        );
    }

    @Test
    void salvarRegistrosValidos() {
        Armazenamento armazenamento = criarArmazenamento();
        MaterialRadioativo material = criarMaterial();
        ArmazenamentoRepository repositorioArmazenamento = mock(ArmazenamentoRepository.class);
        MaterialRadioativoRepository repositorioMaterial = mock(MaterialRadioativoRepository.class);
        when(repositorioArmazenamento.save(armazenamento)).thenReturn(armazenamento);
        when(repositorioMaterial.save(material)).thenReturn(material);

        assertSame(armazenamento, new ArmazenamentoService(repositorioArmazenamento).salvar(armazenamento));
        assertSame(material, new MaterialRadioativoService(repositorioMaterial).salvar(material));
    }

    private Armazenamento criarArmazenamento() {
        MaterialRadioativo material = criarMaterial();
        material.setId(1L);
        Recipiente recipiente = new Recipiente();
        recipiente.setId(2L);
        Responsavel responsavel = new Responsavel();
        responsavel.setId(3L);
        return new Armazenamento(null, "2026-11-17", 1f, "", material, recipiente, responsavel);
    }

    private MaterialRadioativo criarMaterial() {
        TipoMaterial tipo = new TipoMaterial();
        tipo.setId(1L);
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setId(2L);
        return new MaterialRadioativo(null, "MAT-1", "Material de teste", 1f, "g", "2026-11-17", tipo, fornecedor);
    }
}
