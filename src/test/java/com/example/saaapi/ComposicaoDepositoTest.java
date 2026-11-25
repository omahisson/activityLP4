package com.example.saaapi;

import com.example.saaapi.exception.RegraNegocioException;
import com.example.saaapi.model.entity.Deposito;
import com.example.saaapi.model.entity.Recipiente;
import com.example.saaapi.model.entity.Sala;
import com.example.saaapi.model.repository.DepositoRepository;
import com.example.saaapi.model.repository.RecipienteRepository;
import com.example.saaapi.model.repository.SalaRepository;
import com.example.saaapi.service.SalaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ComposicaoDepositoTest {

    @Autowired private MockMvc cliente;
    @Autowired private ObjectMapper mapeador;
    @Autowired private DepositoRepository repositorioDeposito;
    @Autowired private SalaRepository repositorioSala;
    @Autowired private RecipienteRepository repositorioRecipiente;
    @Autowired private SalaService servicoSala;
    @Autowired private EntityManager gerenciador;

    @Test
    void cadastrarSalaPeloDepositoEConsultarNosDoisEndpoints() throws Exception {
        Deposito deposito = criarDeposito();
        cliente.perform(post("/api/v1/salas").contentType(MediaType.APPLICATION_JSON)
                        .content(mapeador.writeValueAsString(Map.of("nome", "Sala A", "descricao", "Sala interna",
                                "idDeposito", deposito.getId()))))
                .andExpect(status().isCreated()).andExpect(jsonPath("idDeposito").value(deposito.getId()));

        gerenciador.flush();
        gerenciador.clear();
        Sala sala = repositorioSala.findAll().get(0);
        assertEquals(deposito.getId(), servicoSala.buscarIdDepositoPorSala(sala.getId()));
        cliente.perform(get("/api/v1/depositos/" + deposito.getId()))
                .andExpect(status().isOk()).andExpect(jsonPath("salas.length()").value(1))
                .andExpect(jsonPath("salas[0].id").value(sala.getId()))
                .andExpect(jsonPath("salas[0].idDeposito").value(deposito.getId()));
        cliente.perform(get("/api/v1/salas/" + sala.getId()))
                .andExpect(status().isOk()).andExpect(jsonPath("idDeposito").value(deposito.getId()));
        cliente.perform(get("/api/v1/salas"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].idDeposito").value(deposito.getId()));
    }

    @Test
    void rejeitarCadastroSemDepositoOuComDepositoInexistente() throws Exception {
        cliente.perform(post("/api/v1/salas").contentType(MediaType.APPLICATION_JSON)
                        .content(mapeador.writeValueAsString(Map.of("nome", "Sala"))))
                .andExpect(status().isBadRequest()).andExpect(content().string("Depósito não informado"));
        cliente.perform(post("/api/v1/salas").contentType(MediaType.APPLICATION_JSON)
                        .content(mapeador.writeValueAsString(Map.of("nome", "Sala", "idDeposito", -1))))
                .andExpect(status().isBadRequest()).andExpect(content().string("Depósito não encontrado"));
        assertEquals(0, repositorioSala.count());
    }

    @Test
    void impedirSalaSemProprietarioNoBanco() {
        assertThrows(DataIntegrityViolationException.class,
                () -> repositorioSala.saveAndFlush(new Sala(null, "Sala isolada", "")));
    }

    @Test
    void impedirCompartilhamentoEAlteracaoDiretaDaColecao() {
        Deposito deposito = criarDeposito();
        Sala sala = servicoSala.salvar(new Sala(null, "Sala", ""), deposito.getId());
        Deposito outroDeposito = criarDeposito();

        assertThrows(RegraNegocioException.class, () -> outroDeposito.adicionarSala(sala));
        assertThrows(UnsupportedOperationException.class,
                () -> deposito.getSalas().add(new Sala(null, "Outra sala", "")));
    }

    @Test
    void impedirTransferenciaDeSalaEntreDepositos() throws Exception {
        Deposito deposito = criarDeposito();
        Sala sala = servicoSala.salvar(new Sala(null, "Sala original", ""), deposito.getId());
        Deposito outroDeposito = criarDeposito();

        cliente.perform(put("/api/v1/salas/" + sala.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(mapeador.writeValueAsString(Map.of("nome", "Sala alterada", "idDeposito", outroDeposito.getId()))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Sala não pode ser transferida para outro depósito"));
        assertEquals(deposito.getId(), servicoSala.buscarIdDepositoPorSala(sala.getId()));
        assertEquals("Sala original", sala.getNome());
    }

    @Test
    void atualizarSalaMantendoSeuProprietario() throws Exception {
        Deposito deposito = criarDeposito();
        Sala sala = servicoSala.salvar(new Sala(null, "Sala original", ""), deposito.getId());

        cliente.perform(put("/api/v1/salas/" + sala.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(mapeador.writeValueAsString(Map.of("nome", "Sala atualizada", "descricao", "Nova descrição",
                                "idDeposito", deposito.getId()))))
                .andExpect(status().isOk()).andExpect(jsonPath("idDeposito").value(deposito.getId()));
        gerenciador.flush();
        gerenciador.clear();
        assertEquals("Sala atualizada", repositorioSala.findById(sala.getId()).orElseThrow().getNome());
        assertEquals(deposito.getId(), servicoSala.buscarIdDepositoPorSala(sala.getId()));
    }

    @Test
    void atualizarDepositoSemRemoverSuasSalas() throws Exception {
        Deposito deposito = criarDeposito();
        Sala sala = servicoSala.salvar(new Sala(null, "Sala", ""), deposito.getId());

        cliente.perform(put("/api/v1/depositos/" + deposito.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(mapeador.writeValueAsString(Map.of("nome", "Depósito atualizado", "endereco", "Novo endereço",
                                "descricao", "Nova descrição", "salas", List.of()))))
                .andExpect(status().isOk()).andExpect(jsonPath("salas.length()").value(1));
        gerenciador.flush();
        gerenciador.clear();
        Deposito atualizado = repositorioDeposito.findById(deposito.getId()).orElseThrow();
        assertEquals("Depósito atualizado", atualizado.getNome());
        assertEquals(sala.getId(), atualizado.getSalas().get(0).getId());
    }

    @Test
    void removerSalaDaComposicaoExcluindoSeuRegistro() throws Exception {
        Deposito deposito = criarDeposito();
        Sala sala = servicoSala.salvar(new Sala(null, "Sala", ""), deposito.getId());

        cliente.perform(delete("/api/v1/salas/" + sala.getId())).andExpect(status().isNoContent());
        gerenciador.flush();
        gerenciador.clear();
        assertFalse(repositorioSala.findById(sala.getId()).isPresent());
        assertTrue(repositorioDeposito.findById(deposito.getId()).orElseThrow().getSalas().isEmpty());
    }

    @Test
    void excluirDepositoExcluindoSomenteSuasSalas() throws Exception {
        Deposito deposito = criarDeposito();
        servicoSala.salvar(new Sala(null, "Sala A", ""), deposito.getId());
        servicoSala.salvar(new Sala(null, "Sala B", ""), deposito.getId());
        Deposito outroDeposito = criarDeposito();
        Sala outraSala = servicoSala.salvar(new Sala(null, "Sala C", ""), outroDeposito.getId());

        cliente.perform(delete("/api/v1/depositos/" + deposito.getId())).andExpect(status().isNoContent());
        gerenciador.flush();
        gerenciador.clear();
        assertFalse(repositorioDeposito.findById(deposito.getId()).isPresent());
        assertEquals(1, repositorioSala.count());
        assertTrue(repositorioSala.findById(outraSala.getId()).isPresent());
    }

    @Test
    void bloquearExclusaoDeSalaUtilizadaPorRecipiente() throws Exception {
        Deposito deposito = criarDeposito();
        Sala sala = servicoSala.salvar(new Sala(null, "Sala", ""), deposito.getId());
        repositorioRecipiente.saveAndFlush(new Recipiente(null, "REC-1", "", sala));

        cliente.perform(delete("/api/v1/salas/" + sala.getId())).andExpect(status().isBadRequest())
                .andExpect(content().string("Sala utilizada por recipiente"));
        assertEquals(1, repositorioSala.count());
        assertEquals(1, repositorioRecipiente.count());
    }

    @Test
    void bloquearExclusaoDeDepositoComSalaUtilizada() throws Exception {
        Deposito deposito = criarDeposito();
        Sala sala = servicoSala.salvar(new Sala(null, "Sala", ""), deposito.getId());
        repositorioRecipiente.saveAndFlush(new Recipiente(null, "REC-1", "", sala));

        cliente.perform(delete("/api/v1/depositos/" + deposito.getId())).andExpect(status().isBadRequest())
                .andExpect(content().string("Depósito possui sala utilizada por recipiente"));
        assertEquals(1, repositorioDeposito.count());
        assertEquals(1, repositorioSala.count());
        assertEquals(1, repositorioRecipiente.count());
    }

    @Test
    void excluirRecipientePreservandoSalaEDeposito() throws Exception {
        Deposito deposito = criarDeposito();
        Sala sala = servicoSala.salvar(new Sala(null, "Sala", ""), deposito.getId());
        Recipiente recipiente = repositorioRecipiente.saveAndFlush(new Recipiente(null, "REC-1", "", sala));

        repositorioRecipiente.delete(recipiente);
        gerenciador.flush();
        gerenciador.clear();
        assertTrue(repositorioSala.findById(sala.getId()).isPresent());
        assertTrue(repositorioDeposito.findById(deposito.getId()).isPresent());
    }

    private Deposito criarDeposito() {
        return repositorioDeposito.saveAndFlush(new Deposito(null, "Depósito", "Endereço", ""));
    }
}
