package com.example.saaapi.service;

import com.example.saaapi.exception.RegraNegocioException;
import com.example.saaapi.model.entity.Responsavel;
import com.example.saaapi.model.repository.ResponsavelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ResponsavelService {

    private ResponsavelRepository repositorio;
    private final UsuarioService servicoUsuario;

    public ResponsavelService(ResponsavelRepository repositorio, UsuarioService servicoUsuario) {
        this.repositorio = repositorio;
        this.servicoUsuario = servicoUsuario;
    }

    public List<Responsavel> listarResponsaveis() {
        return repositorio.findAll();
    }

    public Optional<Responsavel> buscarResponsavelPorId(Long id) {
        return repositorio.findById(id);
    }

    @Transactional
    public Responsavel salvar(Responsavel responsavel) {
        validar(responsavel);
        return repositorio.save(responsavel);
    }

    @Transactional
    public void excluir(Responsavel responsavel) {
        Objects.requireNonNull(responsavel.getId());
        repositorio.delete(responsavel);
    }

    public void validar(Responsavel responsavel) {
        servicoUsuario.validar(responsavel);
        if (responsavel.getNome() == null || responsavel.getNome().trim().equals("")) {
            throw new RegraNegocioException("Nome inválido");
        }
        if (responsavel.getEmail() == null || responsavel.getEmail().trim().equals("")) {
            throw new RegraNegocioException("Email inválido");
        }
    }
}
