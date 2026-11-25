package com.example.saaapi.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@PrimaryKeyJoinColumn(name = "id")
public class Responsavel extends Usuario {

    private String nome;

    private String email;

    private String celular;
}
