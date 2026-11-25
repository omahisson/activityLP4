package com.example.saaapi.model.entity;

import com.example.saaapi.exception.RegraNegocioException;
import com.example.saaapi.model.validacao.IConsultaCadastroSala;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class DepositoMockTest {

    @Test
    void deveAdicionarSalaQuandoNaoCadastrada() {
        Deposito deposito = new Deposito();
        Sala sala = new Sala();
        IConsultaCadastroSala consultaCadastro = createMock(IConsultaCadastroSala.class);
        expect(consultaCadastro.estaCadastrada(sala)).andReturn(false);
        replay(consultaCadastro);

        deposito.adicionarSala(sala, consultaCadastro);

        assertEquals(1, deposito.getSalas().size());
        assertTrue(deposito.getSalas().contains(sala));
        assertSame(sala, deposito.getSalas().get(0));
        verify(consultaCadastro);
    }

    @Test
    void deveRejeitarSalaQuandoJaCadastrada() {
        Deposito deposito = new Deposito();
        Sala sala = new Sala();
        IConsultaCadastroSala consultaCadastro = createMock(IConsultaCadastroSala.class);
        expect(consultaCadastro.estaCadastrada(sala)).andReturn(true);
        replay(consultaCadastro);

        try {
            deposito.adicionarSala(sala, consultaCadastro);
            fail("Deveria rejeitar uma sala já cadastrada");
        } catch (Exception excecao) {
            assertEquals(RegraNegocioException.class, excecao.getClass());
            assertEquals("Sala já cadastrada não pode ser adicionada a outro depósito", excecao.getMessage());
        }

        assertTrue(deposito.getSalas().isEmpty());
        verify(consultaCadastro);
    }
}
