package com.example.saaapi.service;

import com.example.saaapi.model.entity.Armazenamento;
import com.example.saaapi.model.repository.ArmazenamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ArmazenamentoService {

    private ArmazenamentoRepository repositorio;

    public ArmazenamentoService(ArmazenamentoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<Armazenamento> listarArmazenamentos() {
        return repositorio.findAll();
    }

    public Optional<Armazenamento> buscarArmazenamentoPorId(Long id) {
        return repositorio.findById(id);
    }

    @Transactional
    public Armazenamento salvar(Armazenamento armazenamento) {
        validar(armazenamento);
        return repositorio.save(armazenamento);
    }

    @Transactional
    public void excluir(Armazenamento armazenamento) {
        Objects.requireNonNull(armazenamento.getId());
        repositorio.delete(armazenamento);
    }

    public void validar(Armazenamento armazenamento) {
        armazenamento.validarDados();
    }
}
