package com.example.saaapi.service;

import com.example.saaapi.exception.RegraNegocioException;
import com.example.saaapi.model.entity.Fornecedor;
import com.example.saaapi.model.repository.FornecedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class FornecedorService {

    private FornecedorRepository repositorio;

    public FornecedorService(FornecedorRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<Fornecedor> listarFornecedores() {
        return repositorio.findAll();
    }

    public Optional<Fornecedor> buscarFornecedorPorId(Long id) {
        return repositorio.findById(id);
    }

    @Transactional
    public Fornecedor salvar(Fornecedor fornecedor) {
        validar(fornecedor);
        return repositorio.save(fornecedor);
    }

    @Transactional
    public void excluir(Fornecedor fornecedor) {
        Objects.requireNonNull(fornecedor.getId());
        repositorio.delete(fornecedor);
    }

    public void validar(Fornecedor fornecedor) {
        if (fornecedor.getNome() == null || fornecedor.getNome().trim().equals("")) {
            throw new RegraNegocioException("Nome inválido");
        }
        if (fornecedor.getCnpj() == null || fornecedor.getCnpj().trim().equals("")) {
            throw new RegraNegocioException("CNPJ inválido");
        }
    }
}
