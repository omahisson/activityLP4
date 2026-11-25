package com.example.saaapi.service;

import com.example.saaapi.exception.RegraNegocioException;
import com.example.saaapi.model.entity.Movimentacao;
import com.example.saaapi.model.repository.MovimentacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MovimentacaoService {

    private MovimentacaoRepository repositorio;

    public MovimentacaoService(MovimentacaoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<Movimentacao> listarMovimentacoes() {
        return repositorio.findAll();
    }

    public Optional<Movimentacao> buscarMovimentacaoPorId(Long id) {
        return repositorio.findById(id);
    }

    @Transactional
    public Movimentacao salvar(Movimentacao movimentacao) {
        validar(movimentacao);
        return repositorio.save(movimentacao);
    }

    @Transactional
    public void excluir(Movimentacao movimentacao) {
        Objects.requireNonNull(movimentacao.getId());
        repositorio.delete(movimentacao);
    }

    public void validar(Movimentacao movimentacao) {
        if (movimentacao.getTipo() == null || movimentacao.getTipo().trim().equals("")) {
            throw new RegraNegocioException("Tipo inválido");
        }
        if (Objects.toString(movimentacao.getDataMovimentacao(), "").trim().equals("")) {
            throw new RegraNegocioException("Data inválida");
        }
        if (movimentacao.getArmazenamento() == null || movimentacao.getArmazenamento().getId() == null) {
            throw new RegraNegocioException("Armazenamento inválido");
        }
        if (movimentacao.getResponsavel() == null || movimentacao.getResponsavel().getId() == null) {
            throw new RegraNegocioException("Responsavel inválido");
        }
        if (movimentacao.getQuantidade() == null || movimentacao.getQuantidade() <= 0) {
            throw new RegraNegocioException("Quantidade inválida");
        }
    }
}
