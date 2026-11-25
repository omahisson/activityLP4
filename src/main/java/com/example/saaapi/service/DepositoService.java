package com.example.saaapi.service;

import com.example.saaapi.exception.RegraNegocioException;
import com.example.saaapi.model.entity.Deposito;
import com.example.saaapi.model.entity.Sala;
import com.example.saaapi.model.repository.DepositoRepository;
import com.example.saaapi.model.repository.RecipienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DepositoService {

    private DepositoRepository repositorio;
    private final RecipienteRepository repositorioRecipiente;

    public DepositoService(DepositoRepository repositorio, RecipienteRepository repositorioRecipiente) {
        this.repositorio = repositorio;
        this.repositorioRecipiente = repositorioRecipiente;
    }

    public List<Deposito> listarDepositos() {
        return repositorio.findAll();
    }

    public Optional<Deposito> buscarDepositoPorId(Long id) {
        return repositorio.findById(id);
    }

    @Transactional
    public Deposito salvar(Deposito deposito) {
        validar(deposito);
        if (deposito.getId() != null) {
            Deposito existente = repositorio.findById(deposito.getId())
                    .orElseThrow(() -> new RegraNegocioException("Depósito não encontrado"));
            existente.setNome(deposito.getNome());
            existente.setEndereco(deposito.getEndereco());
            existente.setDescricao(deposito.getDescricao());
            return repositorio.save(existente);
        }
        return repositorio.save(deposito);
    }

    @Transactional
    public void excluir(Deposito deposito) {
        Objects.requireNonNull(deposito.getId());
        Deposito existente = repositorio.findById(deposito.getId())
                .orElseThrow(() -> new RegraNegocioException("Depósito não encontrado"));
        for (Sala sala : existente.getSalas()) {
            if (repositorioRecipiente.contarPorIdSala(sala.getId()) > 0) {
                throw new RegraNegocioException("Depósito possui sala utilizada por recipiente");
            }
        }
        repositorio.delete(existente);
    }

    public void validar(Deposito deposito) {
        if (deposito.getNome() == null || deposito.getNome().trim().equals("")) {
            throw new RegraNegocioException("Nome inválido");
        }
    }
}
