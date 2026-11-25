package com.example.saaapi.service;

import com.example.saaapi.model.entity.MaterialRadioativo;
import com.example.saaapi.model.repository.MaterialRadioativoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MaterialRadioativoService {

    private MaterialRadioativoRepository repositorio;

    public MaterialRadioativoService(MaterialRadioativoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<MaterialRadioativo> listarMateriaisRadioativos() {
        return repositorio.findAll();
    }

    public Optional<MaterialRadioativo> buscarMaterialRadioativoPorId(Long id) {
        return repositorio.findById(id);
    }

    @Transactional
    public MaterialRadioativo salvar(MaterialRadioativo materialRadioativo) {
        validar(materialRadioativo);
        return repositorio.save(materialRadioativo);
    }

    @Transactional
    public void excluir(MaterialRadioativo materialRadioativo) {
        Objects.requireNonNull(materialRadioativo.getId());
        repositorio.delete(materialRadioativo);
    }

    public void validar(MaterialRadioativo materialRadioativo) {
        materialRadioativo.validarDados();
    }
}
