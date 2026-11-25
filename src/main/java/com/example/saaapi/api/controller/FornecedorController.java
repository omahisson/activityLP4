package com.example.saaapi.api.controller;

import com.example.saaapi.api.dto.FornecedorDTO;
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
@RequestMapping("/api/v1/fornecedores")
@RequiredArgsConstructor
@CrossOrigin
public class FornecedorController {

    private final FornecedorService servico;

    @GetMapping()
    public ResponseEntity listar() {
        List<Fornecedor> registros = servico.listarFornecedores();
        return ResponseEntity.ok(registros.stream().map(FornecedorDTO::criar).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity buscarPorId(@PathVariable("id") Long id) {
        Optional<Fornecedor> registro = servico.buscarFornecedorPorId(id);
        if (!registro.isPresent()) {
            return new ResponseEntity("Fornecedor não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(registro.map(FornecedorDTO::criar));
    }

    @PostMapping()
    public ResponseEntity cadastrar(@RequestBody FornecedorDTO dados) {
        try {
            Fornecedor fornecedor = converter(dados);
            fornecedor = servico.salvar(fornecedor);
            return new ResponseEntity(fornecedor, HttpStatus.CREATED);
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody FornecedorDTO dados) {
        if (!servico.buscarFornecedorPorId(id).isPresent()) {
            return new ResponseEntity("Fornecedor não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            Fornecedor fornecedor = converter(dados);
            fornecedor.setId(id);
            servico.salvar(fornecedor);
            return ResponseEntity.ok(fornecedor);
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<Fornecedor> registro = servico.buscarFornecedorPorId(id);
        if (!registro.isPresent()) {
            return new ResponseEntity("Fornecedor não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            servico.excluir(registro.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    public Fornecedor converter(FornecedorDTO dados) {
        ModelMapper mapeador = new ModelMapper();
        Fornecedor fornecedor = mapeador.map(dados, Fornecedor.class);
        return fornecedor;
    }
}
