package com.example.saaapi.service;

import com.example.saaapi.exception.RegraNegocioException;
import com.example.saaapi.model.entity.Sala;
import com.example.saaapi.model.entity.Deposito;
import com.example.saaapi.model.repository.SalaRepository;
import com.example.saaapi.model.repository.DepositoRepository;
import com.example.saaapi.model.repository.RecipienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SalaService {

    private SalaRepository repositorio;
    private final DepositoRepository repositorioDeposito;
    private final RecipienteRepository repositorioRecipiente;

    public SalaService(SalaRepository repositorio, DepositoRepository repositorioDeposito,
                       RecipienteRepository repositorioRecipiente) {
        this.repositorio = repositorio;
        this.repositorioDeposito = repositorioDeposito;
        this.repositorioRecipiente = repositorioRecipiente;
    }

    public List<Sala> listarSalas() {
        return repositorio.findAll();
    }

    public Optional<Sala> buscarSalaPorId(Long id) {
        return repositorio.findById(id);
    }

    @Transactional
    public Sala salvar(Sala sala, Long idDeposito) {
        validar(sala);
        if (idDeposito == null) {
            throw new RegraNegocioException("Depósito não informado");
        }
        Deposito deposito = repositorioDeposito.findById(idDeposito)
                .orElseThrow(() -> new RegraNegocioException("Depósito não encontrado"));
        Sala registro = sala;
        if (sala.getId() == null) {
            deposito.adicionarSala(sala);
        } else {
            registro = atualizarSala(sala, deposito);
        }
        repositorioDeposito.flush();
        return registro;
    }

    private Sala atualizarSala(Sala sala, Deposito deposito) {
        Long idDepositoAtual = buscarIdDepositoPorSala(sala.getId());
        if (!Objects.equals(idDepositoAtual, deposito.getId())) {
            throw new RegraNegocioException("Sala não pode ser transferida para outro depósito");
        }
        Sala existente = repositorio.findById(sala.getId())
                .orElseThrow(() -> new RegraNegocioException("Sala não encontrada"));
        existente.setNome(sala.getNome());
        existente.setDescricao(sala.getDescricao());
        return existente;
    }

    public Long buscarIdDepositoPorSala(Long idSala) {
        return repositorioDeposito.buscarPorIdSala(idSala)
                .orElseThrow(() -> new RegraNegocioException("Depósito da sala não encontrado"))
                .getId();
    }

    @Transactional
    public void excluir(Sala sala) {
        Objects.requireNonNull(sala.getId());
        if (repositorioRecipiente.contarPorIdSala(sala.getId()) > 0) {
            throw new RegraNegocioException("Sala utilizada por recipiente");
        }
        Deposito deposito = repositorioDeposito.buscarPorIdSala(sala.getId())
                .orElseThrow(() -> new RegraNegocioException("Depósito da sala não encontrado"));
        deposito.removerSala(sala.getId());
        repositorioDeposito.flush();
    }

    public void validar(Sala sala) {
        if (sala.getNome() == null || sala.getNome().trim().equals("")) {
            throw new RegraNegocioException("Nome inválido");
        }
    }
}
