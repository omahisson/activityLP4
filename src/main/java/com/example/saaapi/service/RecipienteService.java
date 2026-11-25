package com.example.saaapi.service;

import com.example.saaapi.exception.RegraNegocioException;
import com.example.saaapi.model.entity.Recipiente;
import com.example.saaapi.model.repository.RecipienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class RecipienteService {

    private RecipienteRepository repositorio;

    public RecipienteService(RecipienteRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<Recipiente> listarRecipientes() {
        return repositorio.findAll();
    }

    public Optional<Recipiente> buscarRecipientePorId(Long id) {
        return repositorio.findById(id);
    }

    @Transactional
    public Recipiente salvar(Recipiente recipiente) {
        validar(recipiente);
        return repositorio.save(recipiente);
    }

    @Transactional
    public void excluir(Recipiente recipiente) {
        Objects.requireNonNull(recipiente.getId());
        repositorio.delete(recipiente);
    }

    public void validar(Recipiente recipiente) {
        if (recipiente.getCodigo() == null || recipiente.getCodigo().trim().equals("")) {
            throw new RegraNegocioException("Código inválido");
        }
        if (recipiente.getSala() == null || recipiente.getSala().getId() == null) {
            throw new RegraNegocioException("Sala inválido");
        }
    }
}
