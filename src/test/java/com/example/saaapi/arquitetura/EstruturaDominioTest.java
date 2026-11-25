package com.example.saaapi.arquitetura;

import com.example.saaapi.model.entity.Responsavel;
import com.example.saaapi.model.entity.Usuario;
import com.example.saaapi.model.entity.Deposito;
import com.example.saaapi.model.entity.Sala;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EstruturaDominioTest {

    @Test
    void manterDezEntidadesUmaHerancaUmaComposicaoEOitoAssociacoes() throws Exception {
        List<Class<?>> entidades = carregarEntidades();
        assertEquals(10, entidades.size());
        assertTrue(entidades.stream().allMatch(entidade -> entidade.isAnnotationPresent(Entity.class)));
        List<Class<?>> subclasses = entidades.stream()
                .filter(entidade -> entidade.getSuperclass() != Object.class).collect(Collectors.toList());
        assertEquals(List.of(Responsavel.class), subclasses);
        assertEquals(Usuario.class, Responsavel.class.getSuperclass());
        assertEquals(InheritanceType.JOINED, Usuario.class.getAnnotation(Inheritance.class).strategy());

        Map<String, String> associacoes = new TreeMap<>();
        Map<String, String> composicoes = new TreeMap<>();
        for (Class<?> entidade : entidades) {
            verificarRelacionamentos(entidade, associacoes, composicoes);
        }
        assertEquals(Map.of("Deposito.salas", "Sala"), composicoes);
        assertEquals(Map.of(
                "Movimentacao.armazenamento", "Armazenamento", "Movimentacao.responsavel", "Responsavel",
                "Armazenamento.materialRadioativo", "MaterialRadioativo", "Armazenamento.recipiente", "Recipiente",
                "Armazenamento.responsavel", "Responsavel", "MaterialRadioativo.tipoMaterial", "TipoMaterial",
                "MaterialRadioativo.fornecedor", "Fornecedor", "Recipiente.sala", "Sala"
        ), associacoes);
    }

    private void verificarRelacionamentos(Class<?> entidade, Map<String, String> associacoes,
                                         Map<String, String> composicoes) throws Exception {
        for (Field campo : entidade.getDeclaredFields()) {
            assertFalse(campo.isAnnotationPresent(ManyToMany.class), campo.toString());
            assertFalse(campo.isAnnotationPresent(OneToOne.class), campo.toString());
            if (campo.isAnnotationPresent(ManyToOne.class)) {
                assertEquals(0, campo.getAnnotation(ManyToOne.class).cascade().length);
                associacoes.put(entidade.getSimpleName() + "." + campo.getName(), campo.getType().getSimpleName());
            }
            if (campo.isAnnotationPresent(OneToMany.class)) {
                verificarComposicao(campo);
                composicoes.put(entidade.getSimpleName() + "." + campo.getName(), "Sala");
            } else {
                assertFalse(Collection.class.isAssignableFrom(campo.getType()), campo.toString());
            }
        }
    }

    private void verificarComposicao(Field campo) throws Exception {
        assertEquals(Deposito.class, campo.getDeclaringClass());
        assertEquals("java.util.List<com.example.saaapi.model.entity.Sala>", campo.getGenericType().getTypeName());
        OneToMany composicao = campo.getAnnotation(OneToMany.class);
        assertTrue(composicao.orphanRemoval());
        assertEquals("", composicao.mappedBy());
        assertArrayEquals(new CascadeType[]{CascadeType.ALL}, composicao.cascade());
        assertFalse(campo.getAnnotation(JoinColumn.class).nullable());
        assertFalse(Arrays.stream(Sala.class.getDeclaredFields()).anyMatch(atributo -> atributo.getType() == Deposito.class));
        assertThrows(NoSuchMethodException.class, () -> Deposito.class.getMethod("setSalas", List.class));
    }

    private List<Class<?>> carregarEntidades() throws Exception {
        List<Path> arquivos;
        try (Stream<Path> caminhos = Files.list(Path.of("src/main/java/com/example/saaapi/model/entity"))) {
            arquivos = caminhos.filter(caminho -> caminho.toString().endsWith(".java")).collect(Collectors.toList());
        }
        List<Class<?>> entidades = new ArrayList<>();
        for (Path arquivo : arquivos) {
            String nome = arquivo.getFileName().toString().replace(".java", "");
            entidades.add(Class.forName("com.example.saaapi.model.entity." + nome));
        }
        return entidades;
    }
}
