package com.example.saaapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.example.saaapi.exception.SenhaInvalidaException;
import com.example.saaapi.model.entity.*;
import com.example.saaapi.model.repository.*;
import com.example.saaapi.service.UsuarioService;
import com.example.saaapi.service.SalaService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SaaapiApplicationTests {

    @Autowired private MockMvc cliente;
    @Autowired private ObjectMapper mapeador;
    @Autowired private UsuarioRepository repositorioUsuario;
    @Autowired private ResponsavelRepository repositorioResponsavel;
    @Autowired private ArmazenamentoRepository repositorioArmazenamento;
    @Autowired private MaterialRadioativoRepository repositorioMaterial;
    @Autowired private TipoMaterialRepository repositorioTipo;
    @Autowired private FornecedorRepository repositorioFornecedor;
    @Autowired private DepositoRepository repositorioDeposito;
    @Autowired private SalaRepository repositorioSala;
    @Autowired private RecipienteRepository repositorioRecipiente;
    @Autowired private PasswordEncoder codificador;
    @Autowired private UsuarioService servicoUsuario;
    @Autowired private SalaService servicoSala;

    @Test
    void carregarContexto() {
        assertNotNull(cliente);
    }

    @Test
    void cadastrarResponsavelComoUsuarioEAutenticar() throws Exception {
        Responsavel responsavel = cadastrarResponsavel();
        Usuario usuario = repositorioUsuario.buscarPorLogin("responsavel").orElseThrow();
        assertTrue(usuario instanceof Responsavel);
        assertEquals(responsavel.getId(), usuario.getId());
        assertEquals("12345678901", usuario.getCpf());
        assertTrue(codificador.matches("senhaInicial", usuario.getSenha()));

        cliente.perform(post("/api/v1/usuarios/auth").contentType(MediaType.APPLICATION_JSON)
                        .content(mapeador.writeValueAsString(Map.of("login", "responsavel", "senha", "senhaInicial"))))
                .andExpect(status().isOk()).andExpect(jsonPath("token").isNotEmpty());
    }

    @Test
    void atualizarPerfilPreservandoCredenciais() throws Exception {
        Responsavel responsavel = cadastrarResponsavel();
        String senhaAnterior = responsavel.getSenha();
        cliente.perform(put("/api/v1/responsaveis/" + responsavel.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(mapeador.writeValueAsString(Map.of("login", "responsavel", "cpf", "12345678901",
                                "nome", "Nome atualizado", "email", "novo@exemplo.com", "celular", "11999999999"))))
                .andExpect(status().isOk()).andExpect(jsonPath("nome").value("Nome atualizado"))
                .andExpect(jsonPath("senha").doesNotExist());

        Responsavel atualizado = repositorioResponsavel.findById(responsavel.getId()).orElseThrow();
        assertEquals(senhaAnterior, atualizado.getSenha());
        assertEquals("Nome atualizado", atualizado.getNome());
    }

    @Test
    void atualizarUsuarioPreservandoEspecializacaoECriptografandoSenha() throws Exception {
        Responsavel responsavel = cadastrarResponsavel();
        cliente.perform(put("/api/v1/usuarios/" + responsavel.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(mapeador.writeValueAsString(Map.of("login", "responsavel", "cpf", "12345678901",
                                "senha", "senhaNova", "senhaRepeticao", "senhaNova", "admin", false))))
                .andExpect(status().isOk()).andExpect(jsonPath("senha").doesNotExist());

        Responsavel atualizado = repositorioResponsavel.findById(responsavel.getId()).orElseThrow();
        assertEquals("Responsável de teste", atualizado.getNome());
        assertTrue(codificador.matches("senhaNova", atualizado.getSenha()));
        Usuario credenciais = new Usuario();
        credenciais.setLogin("responsavel");
        credenciais.setSenha("senhaNova");
        assertNotNull(servicoUsuario.autenticar(credenciais));
        credenciais.setSenha("senhaInicial");
        assertThrows(SenhaInvalidaException.class, () -> servicoUsuario.autenticar(credenciais));
    }

    @Test
    void rejeitarSenhasDiferentesELoginDuplicado() throws Exception {
        cliente.perform(post("/api/v1/responsaveis").contentType(MediaType.APPLICATION_JSON)
                        .content(mapeador.writeValueAsString(Map.of("login", "responsavel", "cpf", "12345678901",
                                "nome", "Nome", "email", "teste@exemplo.com", "senha", "umaSenha", "senhaRepeticao", "outraSenha"))))
                .andExpect(status().isBadRequest());
        assertEquals(0, repositorioUsuario.count());
        cadastrarResponsavel();
        cliente.perform(post("/api/v1/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content(mapeador.writeValueAsString(Map.of("login", "responsavel", "cpf", "12345678901",
                                "senha", "senhaNova", "senhaRepeticao", "senhaNova"))))
                .andExpect(status().isBadRequest()).andExpect(content().string("Login já cadastrado"));
        assertEquals(1, repositorioUsuario.count());
    }

    @Test
    void impedirAutenticacaoDeResponsavelSemSenha() {
        Responsavel responsavel = new Responsavel();
        responsavel.setLogin("responsavelSemSenha");
        responsavel.setCpf("12345678901");
        repositorioResponsavel.saveAndFlush(responsavel);
        assertThrows(UsernameNotFoundException.class,
                () -> servicoUsuario.loadUserByUsername("responsavelSemSenha"));
    }

    @Test
    void rejeitarTrocaParaLoginDeOutraConta() throws Exception {
        Responsavel responsavel = cadastrarResponsavel();
        Usuario outroUsuario = new Usuario(null, "loginOcupado", "98765432100", codificador.encode("senha"), false);
        repositorioUsuario.saveAndFlush(outroUsuario);

        cliente.perform(put("/api/v1/usuarios/" + responsavel.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(mapeador.writeValueAsString(Map.of("login", "loginOcupado", "cpf", "12345678901",
                                "senha", "senhaNova", "senhaRepeticao", "senhaNova"))))
                .andExpect(status().isBadRequest()).andExpect(content().string("Login já cadastrado"));
    }

    @Test
    void persistirAssociacoesComResponsavelEspecializado() throws Exception {
        Responsavel responsavel = cadastrarResponsavel();
        TipoMaterial tipo = repositorioTipo.save(new TipoMaterial(null, "Tipo", "Descrição"));
        Fornecedor fornecedor = repositorioFornecedor.save(new Fornecedor(null, "Fornecedor", "12345678000190", "", ""));
        MaterialRadioativo material = repositorioMaterial.save(new MaterialRadioativo(null, "MAT-1", "Material", 1f,
                "g", "2026-11-17", tipo, fornecedor));
        Deposito deposito = repositorioDeposito.save(new Deposito(null, "Depósito", "Endereço", ""));
        Sala sala = servicoSala.salvar(new Sala(null, "Sala", ""), deposito.getId());
        Recipiente recipiente = repositorioRecipiente.save(new Recipiente(null, "REC-1", "", sala));

        cliente.perform(post("/api/v1/armazenamentos").contentType(MediaType.APPLICATION_JSON)
                        .content(mapeador.writeValueAsString(Map.of("dataEntrada", "2026-11-17", "quantidade", 1,
                                "idMaterialRadioativo", material.getId(), "idRecipiente", recipiente.getId(),
                                "idResponsavel", responsavel.getId()))))
                .andExpect(status().isCreated()).andExpect(jsonPath("responsavel.senha").doesNotExist());

        Armazenamento armazenamento = repositorioArmazenamento.findAll().get(0);
        assertEquals(responsavel.getId(), armazenamento.getResponsavel().getId());
        assertEquals(deposito.getId(), servicoSala.buscarIdDepositoPorSala(armazenamento.getRecipiente().getSala().getId()));

        cliente.perform(delete("/api/v1/armazenamentos/" + armazenamento.getId())).andExpect(status().isNoContent());
        repositorioArmazenamento.flush();
        assertFalse(repositorioArmazenamento.findById(armazenamento.getId()).isPresent());
        assertTrue(repositorioMaterial.findById(material.getId()).isPresent());
        assertTrue(repositorioRecipiente.findById(recipiente.getId()).isPresent());
        assertTrue(repositorioResponsavel.findById(responsavel.getId()).isPresent());
    }

    @Test
    void excluirResponsavelEContaHerdada() throws Exception {
        Responsavel responsavel = cadastrarResponsavel();
        cliente.perform(delete("/api/v1/responsaveis/" + responsavel.getId())).andExpect(status().isNoContent());
        repositorioResponsavel.flush();
        assertFalse(repositorioResponsavel.findById(responsavel.getId()).isPresent());
        assertFalse(repositorioUsuario.findById(responsavel.getId()).isPresent());
    }

    private Responsavel cadastrarResponsavel() throws Exception {
        cliente.perform(post("/api/v1/responsaveis").contentType(MediaType.APPLICATION_JSON)
                        .content(mapeador.writeValueAsString(Map.of("login", "responsavel", "cpf", "12345678901",
                                "nome", "Responsável de teste", "email", "teste@exemplo.com",
                                "senha", "senhaInicial", "senhaRepeticao", "senhaInicial"))))
                .andExpect(status().isCreated()).andExpect(jsonPath("senha").doesNotExist())
                .andExpect(jsonPath("senhaRepeticao").doesNotExist());
        return repositorioResponsavel.findAll().get(0);
    }
}
