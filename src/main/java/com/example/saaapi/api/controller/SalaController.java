package com.example.saaapi.api.controller;

import com.example.saaapi.api.dto.SalaDTO;
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
@RequestMapping("/api/v1/salas")
@RequiredArgsConstructor
@CrossOrigin
public class SalaController {

    private final SalaService servico;

    @GetMapping()
    public ResponseEntity listar() {
        List<Sala> registros = servico.listarSalas();
        return ResponseEntity.ok(registros.stream().map(this::criarDados).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity buscarPorId(@PathVariable("id") Long id) {
        Optional<Sala> registro = servico.buscarSalaPorId(id);
        if (!registro.isPresent()) {
            return new ResponseEntity("Sala não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(criarDados(registro.get()));
    }

    @PostMapping()
    public ResponseEntity cadastrar(@RequestBody SalaDTO dados) {
        try {
            Sala sala = converter(dados);
            sala.setId(null);
            sala = servico.salvar(sala, dados.getIdDeposito());
            return new ResponseEntity(criarDados(sala), HttpStatus.CREATED);
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody SalaDTO dados) {
        if (!servico.buscarSalaPorId(id).isPresent()) {
            return new ResponseEntity("Sala não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            Sala sala = converter(dados);
            sala.setId(id);
            sala = servico.salvar(sala, dados.getIdDeposito());
            return ResponseEntity.ok(criarDados(sala));
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<Sala> registro = servico.buscarSalaPorId(id);
        if (!registro.isPresent()) {
            return new ResponseEntity("Sala não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            servico.excluir(registro.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    public Sala converter(SalaDTO dados) {
        return new Sala(dados.getId(), dados.getNome(), dados.getDescricao());
    }

    private SalaDTO criarDados(Sala sala) {
        return SalaDTO.criar(sala, servico.buscarIdDepositoPorSala(sala.getId()));
    }
}
