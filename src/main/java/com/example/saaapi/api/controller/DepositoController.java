package com.example.saaapi.api.controller;

import com.example.saaapi.api.dto.DepositoDTO;
import com.example.saaapi.exception.RegraNegocioException;
import com.example.saaapi.model.entity.*;
import com.example.saaapi.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/depositos")
@RequiredArgsConstructor
@CrossOrigin
public class DepositoController {

    private final DepositoService servico;

    @GetMapping()
    public ResponseEntity listar() {
        List<Deposito> registros = servico.listarDepositos();
        return ResponseEntity.ok(registros.stream().map(DepositoDTO::criar).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity buscarPorId(@PathVariable("id") Long id) {
        Optional<Deposito> registro = servico.buscarDepositoPorId(id);
        if (!registro.isPresent()) {
            return new ResponseEntity("Depósito não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(registro.map(DepositoDTO::criar));
    }

    @PostMapping()
    public ResponseEntity cadastrar(@RequestBody DepositoDTO dados) {
        try {
            Deposito deposito = converter(dados);
            deposito = servico.salvar(deposito);
            return new ResponseEntity(DepositoDTO.criar(deposito), HttpStatus.CREATED);
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody DepositoDTO dados) {
        if (!servico.buscarDepositoPorId(id).isPresent()) {
            return new ResponseEntity("Depósito não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            Deposito deposito = converter(dados);
            deposito.setId(id);
            deposito = servico.salvar(deposito);
            return ResponseEntity.ok(DepositoDTO.criar(deposito));
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<Deposito> registro = servico.buscarDepositoPorId(id);
        if (!registro.isPresent()) {
            return new ResponseEntity("Depósito não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            servico.excluir(registro.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    public Deposito converter(DepositoDTO dados) {
        return new Deposito(null, dados.getNome(), dados.getEndereco(), dados.getDescricao());
    }
}
