package com.example.saaapi.service;

import com.example.saaapi.exception.RegraNegocioException;
import com.example.saaapi.model.entity.TipoMaterial;
import com.example.saaapi.model.repository.TipoMaterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TipoMaterialService {

    private TipoMaterialRepository repositorio;

    public TipoMaterialService(TipoMaterialRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<TipoMaterial> listarTiposMateriais() {
        return repositorio.findAll();
    }

    public Optional<TipoMaterial> buscarTipoMaterialPorId(Long id) {
        return repositorio.findById(id);
    }

    @Transactional
    public TipoMaterial salvar(TipoMaterial tipoMaterial) {
        validar(tipoMaterial);
        return repositorio.save(tipoMaterial);
    }

    @Transactional
    public void excluir(TipoMaterial tipoMaterial) {
        Objects.requireNonNull(tipoMaterial.getId());
        repositorio.delete(tipoMaterial);
    }

    public void validar(TipoMaterial tipoMaterial) {
        if (tipoMaterial.getNome() == null || tipoMaterial.getNome().trim().equals("")) {
            throw new RegraNegocioException("Nome inválido");
        }
    }
}
