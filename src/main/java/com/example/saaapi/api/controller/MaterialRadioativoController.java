package com.example.saaapi.api.controller;

import com.example.saaapi.api.dto.MaterialRadioativoDTO;
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
@RequestMapping("/api/v1/materiaisradioativos")
@RequiredArgsConstructor
@CrossOrigin
public class MaterialRadioativoController {

    private final MaterialRadioativoService servico;
    private final TipoMaterialService servicoTipoMaterial;
    private final FornecedorService servicoFornecedor;

    @GetMapping()
    public ResponseEntity listar() {
        List<MaterialRadioativo> registros = servico.listarMateriaisRadioativos();
        return ResponseEntity.ok(registros.stream().map(MaterialRadioativoDTO::criar).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity buscarPorId(@PathVariable("id") Long id) {
        Optional<MaterialRadioativo> registro = servico.buscarMaterialRadioativoPorId(id);
        if (!registro.isPresent()) {
            return new ResponseEntity("Material radioativo não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(registro.map(MaterialRadioativoDTO::criar));
    }

    @PostMapping()
    public ResponseEntity cadastrar(@RequestBody MaterialRadioativoDTO dados) {
        try {
            MaterialRadioativo materialRadioativo = converter(dados);
            materialRadioativo = servico.salvar(materialRadioativo);
            return new ResponseEntity(materialRadioativo, HttpStatus.CREATED);
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody MaterialRadioativoDTO dados) {
        if (!servico.buscarMaterialRadioativoPorId(id).isPresent()) {
            return new ResponseEntity("Material radioativo não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            MaterialRadioativo materialRadioativo = converter(dados);
            materialRadioativo.setId(id);
            servico.salvar(materialRadioativo);
            return ResponseEntity.ok(materialRadioativo);
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<MaterialRadioativo> registro = servico.buscarMaterialRadioativoPorId(id);
        if (!registro.isPresent()) {
            return new ResponseEntity("Material radioativo não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            servico.excluir(registro.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    public MaterialRadioativo converter(MaterialRadioativoDTO dados) {
        ModelMapper mapeador = new ModelMapper();
        MaterialRadioativo materialRadioativo = mapeador.map(dados, MaterialRadioativo.class);
        if (dados.getIdTipoMaterial() != null) {
            Optional<TipoMaterial> tipoMaterial = servicoTipoMaterial.buscarTipoMaterialPorId(dados.getIdTipoMaterial());
            if (!tipoMaterial.isPresent()) {
                materialRadioativo.setTipoMaterial(null);
            } else {
                materialRadioativo.setTipoMaterial(tipoMaterial.get());
            }
        }
        if (dados.getIdFornecedor() != null) {
            Optional<Fornecedor> fornecedor = servicoFornecedor.buscarFornecedorPorId(dados.getIdFornecedor());
            if (!fornecedor.isPresent()) {
                materialRadioativo.setFornecedor(null);
            } else {
                materialRadioativo.setFornecedor(fornecedor.get());
            }
        }
        return materialRadioativo;
    }
}
