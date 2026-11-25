package com.example.saaapi.api.controller;

import com.example.saaapi.api.dto.MovimentacaoDTO;
import com.example.saaapi.exception.RegraNegocioException;
import com.example.saaapi.model.entity.*;
import com.example.saaapi.service.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/movimentacoes")
@RequiredArgsConstructor
@CrossOrigin
public class MovimentacaoController {

    private final MovimentacaoService servico;
    private final ArmazenamentoService servicoArmazenamento;
    private final ResponsavelService servicoResponsavel;

    @GetMapping()
    public ResponseEntity listar() {
        List<Movimentacao> registros = servico.listarMovimentacoes();
        return ResponseEntity.ok(registros.stream().map(MovimentacaoDTO::criar).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity buscarPorId(@PathVariable("id") Long id) {
        Optional<Movimentacao> registro = servico.buscarMovimentacaoPorId(id);
        if (!registro.isPresent()) {
            return new ResponseEntity("Movimentação não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(registro.map(MovimentacaoDTO::criar));
    }

    @PostMapping()
    public ResponseEntity cadastrar(@RequestBody MovimentacaoDTO dados) {
        try {
            Movimentacao movimentacao = converter(dados);
            movimentacao = servico.salvar(movimentacao);
            return new ResponseEntity(movimentacao, HttpStatus.CREATED);
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody MovimentacaoDTO dados) {
        if (!servico.buscarMovimentacaoPorId(id).isPresent()) {
            return new ResponseEntity("Movimentação não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            Movimentacao movimentacao = converter(dados);
            movimentacao.setId(id);
            servico.salvar(movimentacao);
            return ResponseEntity.ok(movimentacao);
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<Movimentacao> registro = servico.buscarMovimentacaoPorId(id);
        if (!registro.isPresent()) {
            return new ResponseEntity("Movimentação não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            servico.excluir(registro.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    public Movimentacao converter(MovimentacaoDTO dados) {
        ModelMapper mapeador = new ModelMapper();
        Movimentacao movimentacao = mapeador.map(dados, Movimentacao.class);
        if (dados.getIdArmazenamento() != null) {
            Optional<Armazenamento> armazenamento = servicoArmazenamento.buscarArmazenamentoPorId(dados.getIdArmazenamento());
            if (!armazenamento.isPresent()) {
                movimentacao.setArmazenamento(null);
            } else {
                movimentacao.setArmazenamento(armazenamento.get());
            }
        }
        if (dados.getIdResponsavel() != null) {
            Optional<Responsavel> responsavel = servicoResponsavel.buscarResponsavelPorId(dados.getIdResponsavel());
            if (!responsavel.isPresent()) {
                movimentacao.setResponsavel(null);
            } else {
                movimentacao.setResponsavel(responsavel.get());
            }
        }
        return movimentacao;
    }
}
