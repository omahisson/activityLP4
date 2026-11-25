package com.example.saaapi.api.controller;

import com.example.saaapi.api.dto.TipoMaterialDTO;
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
@RequestMapping("/api/v1/tiposmateriais")
@RequiredArgsConstructor
@CrossOrigin
public class TipoMaterialController {

    private final TipoMaterialService servico;

    @GetMapping()
    public ResponseEntity listar() {
        List<TipoMaterial> registros = servico.listarTiposMateriais();
        return ResponseEntity.ok(registros.stream().map(TipoMaterialDTO::criar).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity buscarPorId(@PathVariable("id") Long id) {
        Optional<TipoMaterial> registro = servico.buscarTipoMaterialPorId(id);
        if (!registro.isPresent()) {
            return new ResponseEntity("Tipo de material não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(registro.map(TipoMaterialDTO::criar));
    }

    @PostMapping()
    public ResponseEntity cadastrar(@RequestBody TipoMaterialDTO dados) {
        try {
            TipoMaterial tipoMaterial = converter(dados);
            tipoMaterial = servico.salvar(tipoMaterial);
            return new ResponseEntity(tipoMaterial, HttpStatus.CREATED);
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody TipoMaterialDTO dados) {
        if (!servico.buscarTipoMaterialPorId(id).isPresent()) {
            return new ResponseEntity("Tipo de material não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            TipoMaterial tipoMaterial = converter(dados);
            tipoMaterial.setId(id);
            servico.salvar(tipoMaterial);
            return ResponseEntity.ok(tipoMaterial);
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<TipoMaterial> registro = servico.buscarTipoMaterialPorId(id);
        if (!registro.isPresent()) {
            return new ResponseEntity("Tipo de material não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            servico.excluir(registro.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    public TipoMaterial converter(TipoMaterialDTO dados) {
        ModelMapper mapeador = new ModelMapper();
        TipoMaterial tipoMaterial = mapeador.map(dados, TipoMaterial.class);
        return tipoMaterial;
    }
}
