package com.example.saaapi.security;

import com.example.saaapi.service.UsuarioService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    private JwtService servicoJwt;
    private UsuarioService servicoUsuario;

    public JwtAuthFilter( JwtService servicoJwt, UsuarioService servicoUsuario ) {
        this.servicoJwt = servicoJwt;
        this.servicoUsuario = servicoUsuario;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest requisicao,
            HttpServletResponse resposta,
            FilterChain cadeiaFiltros) throws ServletException, IOException {

        String autorizacao = requisicao.getHeader("Authorization");

        if( autorizacao != null && autorizacao.startsWith("Bearer")){
            String token = autorizacao.split(" ")[1];
            boolean valido = servicoJwt.validarToken(token);

            if(valido){
                String loginDoUsuario = servicoJwt.obterLoginDoUsuario(token);
                UserDetails usuario = servicoUsuario.loadUserByUsername(loginDoUsuario);
                UsernamePasswordAuthenticationToken detalhesUsuario = new
                        UsernamePasswordAuthenticationToken(usuario,null,
                        usuario.getAuthorities());
                detalhesUsuario.setDetails(new WebAuthenticationDetailsSource().buildDetails(requisicao));
                SecurityContextHolder.getContext().setAuthentication(detalhesUsuario);
            }
        }

        cadeiaFiltros.doFilter(requisicao, resposta);
    }
}
