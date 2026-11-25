package com.example.saaapi.model.entity;

import com.example.saaapi.exception.RegraNegocioException;
import com.example.saaapi.model.validacao.IConsultaCadastroSala;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class DepositoTest {

    @Test
    void deveAdicionarSalaQuandoNaoCadastrada() {
        Deposito deposito = new Deposito();
        Sala sala = new Sala();

        deposito.adicionarSala(sala);

        assertEquals(List.of(sala), deposito.getSalas());
    }

    @Test
    void deveRejeitarSalaQuandoJaCadastrada() {
        Deposito deposito = new Deposito();
        Sala sala = new Sala();
        sala.setId(1L);

        try {
            deposito.adicionarSala(sala);
            fail("Deveria rejeitar uma sala já cadastrada");
        } catch (RegraNegocioException excecao) {
            assertEquals("Sala já cadastrada não pode ser adicionada a outro depósito", excecao.getMessage());
        }
    }

    @Test
    void deveAdicionarSalaQuandoNaoCadastradaMock() {
        Deposito deposito = new Deposito();
        Sala sala = new Sala();
        IConsultaCadastroSala consultaCadastro = createMock(IConsultaCadastroSala.class);
        expect(consultaCadastro.estaCadastrada(sala)).andReturn(false);
        replay(consultaCadastro);

        deposito.adicionarSala(sala, consultaCadastro);

        assertEquals(List.of(sala), deposito.getSalas());
        verify(consultaCadastro);
    }

    @Test
    void deveRejeitarSalaQuandoJaCadastradaMock() {
        Deposito deposito = new Deposito();
        Sala sala = new Sala();
        IConsultaCadastroSala consultaCadastro = createMock(IConsultaCadastroSala.class);

        expect(consultaCadastro.estaCadastrada(sala)).andReturn(true);
        replay(consultaCadastro);

        try {
            deposito.adicionarSala(sala, consultaCadastro);
            fail("Deveria rejeitar uma sala já cadastrada");
        } catch (RegraNegocioException excecao) {
            assertEquals("Sala já cadastrada não pode ser adicionada a outro depósito", excecao.getMessage());
        }

        verify(consultaCadastro);
    }
}
