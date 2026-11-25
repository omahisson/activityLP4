package com.example.saaapi.api.controller;

import com.example.saaapi.api.dto.*;
import com.example.saaapi.exception.RegraNegocioException;
import com.example.saaapi.exception.SenhaInvalidaException;
import com.example.saaapi.model.entity.Usuario;
import com.example.saaapi.security.JwtService;
import com.example.saaapi.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@CrossOrigin
public class UsuarioController {

    private final JwtService servicoJwt;

    private final UsuarioService servico;

    @GetMapping()
    public ResponseEntity listar() {
        List<Usuario> usuarios = servico.listarUsuarios();
        return ResponseEntity.ok(usuarios.stream().map(UsuarioDTO::criar).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity buscarPorId(@PathVariable("id") Long id) {
        Optional<Usuario> usuario = servico.buscarUsuarioPorId(id);
        if (!usuario.isPresent()) {
            return new ResponseEntity("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(usuario.map(UsuarioDTO::criar));
    }

    @PostMapping()
    public ResponseEntity cadastrar(@RequestBody UsuarioDTO dados) {
        try {
            Usuario usuario = converter(dados);
            usuario.setId(null);
            servico.definirSenha(usuario, dados.getSenha(), dados.getSenhaRepeticao());
            usuario = servico.salvar(usuario);
            return new ResponseEntity(UsuarioDTO.criar(usuario), HttpStatus.CREATED);
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    @PostMapping("/auth")
    public TokenDTO autenticar(@RequestBody CredenciaisDTO credenciais){
        try{
            Usuario usuario = new Usuario();
            usuario.setLogin(credenciais.getLogin());
            usuario.setSenha(credenciais.getSenha());
            servico.autenticar(usuario);
            String token = servicoJwt.gerarToken(usuario);
            return new TokenDTO(usuario.getLogin(), token);
        } catch (UsernameNotFoundException | SenhaInvalidaException excecao ){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, excecao.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody UsuarioDTO dados) {
        Optional<Usuario> registro = servico.buscarUsuarioPorId(id);
        if (registro.isEmpty()) {
            return new ResponseEntity("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            Usuario usuario = registro.get();
            servico.definirSenha(usuario, dados.getSenha(), dados.getSenhaRepeticao());
            usuario.setLogin(dados.getLogin());
            usuario.setCpf(dados.getCpf());
            usuario.setAdmin(dados.isAdmin());
            servico.salvar(usuario);
            return ResponseEntity.ok(UsuarioDTO.criar(usuario));
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<Usuario> usuario = servico.buscarUsuarioPorId(id);
        if (!usuario.isPresent()) {
            return new ResponseEntity("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            servico.excluir(usuario.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException excecao) {
            return ResponseEntity.badRequest().body(excecao.getMessage());
        }
    }

    public Usuario converter(UsuarioDTO dados) {
        ModelMapper mapeador = new ModelMapper();
        Usuario usuario = mapeador.map(dados, Usuario.class);
        return usuario;
    }
}
