package com.example.saaapi.api.controller;

import com.example.saaapi.api.dto.ResponsavelDTO;
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
@RequestMapping("/api/v1/responsaveis")
@RequiredArgsConstructor
@CrossOrigin
public class ResponsavelController {

    private final ResponsavelService servico;
    private final UsuarioService servicoUsuario;

    @GetMapping()
    public ResponseEntity listar() {
        List<Responsavel> registros = servico.listarResponsaveis();
        return ResponseEntity.ok(registros.stream().map(ResponsavelDTO::criar).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity buscarPorId(@PathVariable("id") Long id) {
        Optional<Responsavel> registro = servico.buscarResponsavelPorId(id);
        if (!registro.isPresent()) {
            return new ResponseEntity("Responsável não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(registro.map(ResponsavelDTO::criar));
    }

    @PostMapping()
    public ResponseEntity cadastrar(@RequestBody ResponsavelDTO dados) {
        try {
            Responsavel responsavel = converter(dados);
            responsavel.setId(null);
            servicoUsuario.definirSenha(responsavel, dados.getSenha(), dados.getSenhaRepeticao());
            responsavel = servico.salvar(responsavel);
            return new ResponseEntity(ResponsavelDTO.criar(responsavel), HttpStatus.CREATED);
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody ResponsavelDTO dados) {
        Optional<Responsavel> registro = servico.buscarResponsavelPorId(id);
        if (registro.isEmpty()) {
            return new ResponseEntity("Responsável não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            Responsavel responsavel = registro.get();
            if (dados.getSenha() != null || dados.getSenhaRepeticao() != null) {
                servicoUsuario.definirSenha(responsavel, dados.getSenha(), dados.getSenhaRepeticao());
            }
            responsavel.setNome(dados.getNome());
            responsavel.setCpf(dados.getCpf());
            responsavel.setEmail(dados.getEmail());
            responsavel.setCelular(dados.getCelular());
            responsavel.setLogin(dados.getLogin());
            servico.salvar(responsavel);
            return ResponseEntity.ok(ResponsavelDTO.criar(responsavel));
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<Responsavel> registro = servico.buscarResponsavelPorId(id);
        if (!registro.isPresent()) {
            return new ResponseEntity("Responsável não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            servico.excluir(registro.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    public Responsavel converter(ResponsavelDTO dados) {
        ModelMapper mapeador = new ModelMapper();
        Responsavel responsavel = mapeador.map(dados, Responsavel.class);
        return responsavel;
    }
}
