package com.example.saaapi.api.controller;

import com.example.saaapi.api.dto.ArmazenamentoDTO;
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
@RequestMapping("/api/v1/armazenamentos")
@RequiredArgsConstructor
@CrossOrigin
public class ArmazenamentoController {

    private final ArmazenamentoService servico;
    private final MaterialRadioativoService servicoMaterialRadioativo;
    private final RecipienteService servicoRecipiente;
    private final ResponsavelService servicoResponsavel;

    @GetMapping()
    public ResponseEntity listar() {
        List<Armazenamento> registros = servico.listarArmazenamentos();
        return ResponseEntity.ok(registros.stream().map(ArmazenamentoDTO::criar).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity buscarPorId(@PathVariable("id") Long id) {
        Optional<Armazenamento> registro = servico.buscarArmazenamentoPorId(id);
        if (!registro.isPresent()) {
            return new ResponseEntity("Armazenamento não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(registro.map(ArmazenamentoDTO::criar));
    }

    @PostMapping()
    public ResponseEntity cadastrar(@RequestBody ArmazenamentoDTO dados) {
        try {
            Armazenamento armazenamento = converter(dados);
            armazenamento = servico.salvar(armazenamento);
            return new ResponseEntity(armazenamento, HttpStatus.CREATED);
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody ArmazenamentoDTO dados) {
        if (!servico.buscarArmazenamentoPorId(id).isPresent()) {
            return new ResponseEntity("Armazenamento não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            Armazenamento armazenamento = converter(dados);
            armazenamento.setId(id);
            servico.salvar(armazenamento);
            return ResponseEntity.ok(armazenamento);
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<Armazenamento> registro = servico.buscarArmazenamentoPorId(id);
        if (!registro.isPresent()) {
            return new ResponseEntity("Armazenamento não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            servico.excluir(registro.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    public Armazenamento converter(ArmazenamentoDTO dados) {
        ModelMapper mapeador = new ModelMapper();
        Armazenamento armazenamento = mapeador.map(dados, Armazenamento.class);
        if (dados.getIdMaterialRadioativo() != null) {
            Optional<MaterialRadioativo> materialRadioativo = servicoMaterialRadioativo.buscarMaterialRadioativoPorId(dados.getIdMaterialRadioativo());
            if (!materialRadioativo.isPresent()) {
                armazenamento.setMaterialRadioativo(null);
            } else {
                armazenamento.setMaterialRadioativo(materialRadioativo.get());
            }
        }
        if (dados.getIdRecipiente() != null) {
            Optional<Recipiente> recipiente = servicoRecipiente.buscarRecipientePorId(dados.getIdRecipiente());
            if (!recipiente.isPresent()) {
                armazenamento.setRecipiente(null);
            } else {
                armazenamento.setRecipiente(recipiente.get());
            }
        }
        if (dados.getIdResponsavel() != null) {
            Optional<Responsavel> responsavel = servicoResponsavel.buscarResponsavelPorId(dados.getIdResponsavel());
            if (!responsavel.isPresent()) {
                armazenamento.setResponsavel(null);
            } else {
                armazenamento.setResponsavel(responsavel.get());
            }
        }
        return armazenamento;
    }
}
