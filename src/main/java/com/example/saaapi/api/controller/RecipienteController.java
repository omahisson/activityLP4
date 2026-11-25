package com.example.saaapi.api.controller;

import com.example.saaapi.api.dto.RecipienteDTO;
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
@RequestMapping("/api/v1/recipientes")
@RequiredArgsConstructor
@CrossOrigin
public class RecipienteController {

    private final RecipienteService servico;
    private final SalaService servicoSala;

    @GetMapping()
    public ResponseEntity listar() {
        List<Recipiente> registros = servico.listarRecipientes();
        return ResponseEntity.ok(registros.stream().map(RecipienteDTO::criar).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity buscarPorId(@PathVariable("id") Long id) {
        Optional<Recipiente> registro = servico.buscarRecipientePorId(id);
        if (!registro.isPresent()) {
            return new ResponseEntity("Recipiente não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(registro.map(RecipienteDTO::criar));
    }

    @PostMapping()
    public ResponseEntity cadastrar(@RequestBody RecipienteDTO dados) {
        try {
            Recipiente recipiente = converter(dados);
            recipiente = servico.salvar(recipiente);
            return new ResponseEntity(recipiente, HttpStatus.CREATED);
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody RecipienteDTO dados) {
        if (!servico.buscarRecipientePorId(id).isPresent()) {
            return new ResponseEntity("Recipiente não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            Recipiente recipiente = converter(dados);
            recipiente.setId(id);
            servico.salvar(recipiente);
            return ResponseEntity.ok(recipiente);
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<Recipiente> registro = servico.buscarRecipientePorId(id);
        if (!registro.isPresent()) {
            return new ResponseEntity("Recipiente não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            servico.excluir(registro.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    public Recipiente converter(RecipienteDTO dados) {
        ModelMapper mapeador = new ModelMapper();
        Recipiente recipiente = mapeador.map(dados, Recipiente.class);
        if (dados.getIdSala() != null) {
            Optional<Sala> sala = servicoSala.buscarSalaPorId(dados.getIdSala());
            if (!sala.isPresent()) {
                recipiente.setSala(null);
            } else {
                recipiente.setSala(sala.get());
            }
        }
        return recipiente;
    }
}
