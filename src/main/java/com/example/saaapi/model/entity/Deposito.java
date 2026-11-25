package com.example.saaapi.model.entity;

import com.example.saaapi.exception.RegraNegocioException;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
public class Deposito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String endereco;

    private String descricao;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "deposito_id", nullable = false)
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Sala> salas = new ArrayList<>();

    public Deposito(Long id, String nome, String endereco, String descricao) { //1
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.descricao = descricao;
    }

    public List<Sala> getSalas() {
        return Collections.unmodifiableList(salas);
    } //1

    public void adicionarSala(Sala sala) {
        Objects.requireNonNull(sala, "Sala não informada");
        if (sala.getId() != null) { //1
            throw new RegraNegocioException("Sala já cadastrada não pode ser adicionada a outro depósito");
        }
        if (salas.contains(sala)) { //2
            throw new RegraNegocioException("Sala já pertence ao depósito");
        }
        salas.add(sala);
    } //3

    public void removerSala(Long idSala) {
        salas.removeIf(sala -> Objects.equals(sala.getId(), idSala));
    } //1
}
