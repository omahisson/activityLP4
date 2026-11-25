package com.example.saaapi.config;

import com.example.saaapi.security.JwtAuthFilter;
import com.example.saaapi.security.JwtService;
import com.example.saaapi.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UsuarioService servicoUsuario;

    @Autowired
    private JwtService servicoJwt;

    @Bean
    PasswordEncoder codificadorSenha() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OncePerRequestFilter filtroJwt(){
        return new JwtAuthFilter(servicoJwt, servicoUsuario);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder autenticacao) throws Exception {
        autenticacao
            .userDetailsService(servicoUsuario)
            .passwordEncoder(codificadorSenha());
    }

    @Override
    protected void configure(HttpSecurity segurancaHttp) throws Exception {
        segurancaHttp
            .cors().disable()
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/api/v1/materiaisradioativos/**")
                    .permitAll()

                .antMatchers("/api/v1/armazenamentos/**")
                    .permitAll()
                .antMatchers("/api/v1/fornecedores/**")
                    .permitAll()
                .antMatchers("/api/v1/responsaveis/**")
                    .permitAll()
                .antMatchers("/api/v1/depositos/**")
                    .permitAll()
                .antMatchers("/api/v1/tiposmateriais/**")
                    .permitAll()
                .antMatchers("/api/v1/movimentacoes/**")
                    .hasAnyRole("ADMIN")
                .antMatchers("/api/v1/recipientes/**")
                    .hasAnyRole("USER", "ADMIN")
                .antMatchers("/api/v1/salas/**")
                    .permitAll()
                .antMatchers( "/api/v1/usuarios/**")
                    .permitAll()
                .anyRequest().authenticated()
            .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .addFilterBefore(filtroJwt(), UsernamePasswordAuthenticationFilter.class);
        ;
    }

    @Override
    public void configure(WebSecurity segurancaWeb) throws Exception {
        segurancaWeb.ignoring().antMatchers(
                "/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**");
    }
}
