package com.example.saaapi.service;

import com.example.saaapi.exception.RegraNegocioException;
import com.example.saaapi.exception.SenhaInvalidaException;
import com.example.saaapi.model.entity.Usuario;
import com.example.saaapi.model.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private PasswordEncoder codificador;

    @Autowired
    private UsuarioRepository repositorio;

    public List<Usuario> listarUsuarios() {
        return repositorio.findAll();
    }

    public Optional<Usuario> buscarUsuarioPorId(Long id) {
        return repositorio.findById(id);
    }

    @Transactional
    public Usuario salvar(Usuario usuario){
        validar(usuario);
        return repositorio.save(usuario);
    }

    public UserDetails autenticar(Usuario usuario){
        UserDetails detalhesUsuario = loadUserByUsername(usuario.getLogin());
        boolean senhasBatem = codificador.matches(usuario.getSenha(), detalhesUsuario.getPassword());

        if (senhasBatem){
            return detalhesUsuario;
        }
        throw new SenhaInvalidaException();
    }

    @Override
    public UserDetails loadUserByUsername(String nomeUsuario) throws UsernameNotFoundException {

        Usuario usuario = repositorio.buscarPorLogin(nomeUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        if (usuario.getSenha() == null) {
            throw new UsernameNotFoundException("Usuário sem senha cadastrada");
        }

        String[] perfis = usuario.isAdmin()
                ? new String[]{"ADMIN", "USER"}
                : new String[]{"USER"};

        return User
                .builder()
                .username(usuario.getLogin())
                .password(usuario.getSenha())
                .roles(perfis)
                .build();
    }

    @Transactional
    public void excluir(Usuario usuario) {
        Objects.requireNonNull(usuario.getId());
        repositorio.delete(usuario);
    }

    public void validar(Usuario usuario) {
        if (usuario.getLogin() == null || usuario.getLogin().trim().equals("")) {
            throw new RegraNegocioException("Login inválido");
        }
        if (usuario.getCpf() == null || usuario.getCpf().trim().equals("")) {
            throw new RegraNegocioException("CPF inválido");
        }
        if (repositorio.contarOutrosUsuariosPorLogin(usuario.getLogin(), usuario.getId()) > 0) {
            throw new RegraNegocioException("Login já cadastrado");
        }
    }

    public void definirSenha(Usuario usuario, String senha, String senhaRepeticao) {
        if (senha == null || senha.isBlank()) {
            throw new RegraNegocioException("Senha inválida");
        }
        if (senhaRepeticao == null || senhaRepeticao.isBlank()) {
            throw new RegraNegocioException("Repetição de senha inválida");
        }
        if (!senha.equals(senhaRepeticao)) {
            throw new RegraNegocioException("Senhas não conferem");
        }
        usuario.setSenha(codificador.encode(senha));
    }
}
