package com.example.saaapi.arquitetura;

import com.sun.source.tree.*;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TreeScanner;
import org.junit.jupiter.api.Test;

import javax.tools.ToolProvider;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ComplexidadeTest {

    private static final Set<Tree.Kind> DECISOES = Set.of(
            Tree.Kind.IF, Tree.Kind.FOR_LOOP, Tree.Kind.ENHANCED_FOR_LOOP,
            Tree.Kind.WHILE_LOOP, Tree.Kind.DO_WHILE_LOOP, Tree.Kind.CATCH,
            Tree.Kind.CONDITIONAL_EXPRESSION, Tree.Kind.CONDITIONAL_AND, Tree.Kind.CONDITIONAL_OR);

    @Test
    void manterSomenteDoisMetodosComComplexidadeOnze() throws Exception {
        Map<String, Integer> complexidades = medirComplexidades();
        Set<String> metodosOnze = complexidades.entrySet().stream()
                .filter(registro -> registro.getValue() == 11)
                .map(Map.Entry::getKey).collect(Collectors.toSet());

        assertEquals(Set.of("Armazenamento#validarDados()", "MaterialRadioativo#validarDados()"), metodosOnze);
        assertTrue(complexidades.values().stream().allMatch(valor -> valor <= 11), complexidades.toString());

        List<String> relatorio = complexidades.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(registro -> registro.getKey() + " = " + registro.getValue()).collect(Collectors.toList());
        Files.createDirectories(Path.of("target"));
        Files.write(Path.of("target/complexidade-ciclomatica.txt"), relatorio, StandardCharsets.UTF_8);
    }

    private Map<String, Integer> medirComplexidades() throws Exception {
        List<Path> arquivos;
        try (Stream<Path> caminhos = Files.walk(Path.of("src"))) {
            arquivos = caminhos.filter(caminho -> caminho.toString().endsWith(".java"))
                    .collect(Collectors.toList());
        }
        var compilador = ToolProvider.getSystemJavaCompiler();
        assertNotNull(compilador, "Execute os testes com o JDK 17");
        Map<String, Integer> complexidades = new TreeMap<>();
        try (var gerenciador = compilador.getStandardFileManager(null, Locale.ROOT, StandardCharsets.UTF_8)) {
            var fontes = gerenciador.getJavaFileObjectsFromPaths(arquivos);
            JavacTask tarefa = (JavacTask) compilador.getTask(null, gerenciador, null,
                    List.of("-proc:none"), null, fontes);
            for (CompilationUnitTree unidade : tarefa.parse()) {
                String classe = Path.of(unidade.getSourceFile().toUri()).getFileName().toString().replace(".java", "");
                new TreeScanner<Void, Void>() {
                    @Override
                    public Void visitMethod(MethodTree metodo, Void contexto) {
                        if (metodo.getBody() != null) {
                            String parametros = metodo.getParameters().stream()
                                    .map(parametro -> parametro.getType().toString()).collect(Collectors.joining(","));
                            String identificador = classe + "#" + metodo.getName() + "(" + parametros + ")";
                            complexidades.put(identificador, medirMetodo(metodo));
                        }
                        return super.visitMethod(metodo, contexto);
                    }
                }.scan(unidade, null);
            }
        }
        return complexidades;
    }

    private int medirMetodo(MethodTree metodo) {
        AtomicInteger complexidade = new AtomicInteger(1);
        new TreeScanner<Void, Void>() {
            @Override
            public Void scan(Tree trecho, Void contexto) {
                if (trecho == null) {
                    return null;
                }
                if (DECISOES.contains(trecho.getKind())) {
                    complexidade.incrementAndGet();
                }
                return super.scan(trecho, contexto);
            }

            @Override
            public Void visitCase(CaseTree alternativa, Void contexto) {
                complexidade.addAndGet(alternativa.getExpressions().size());
                return super.visitCase(alternativa, contexto);
            }

            @Override
            public Void visitClass(ClassTree classe, Void contexto) {
                return null;
            }
        }.scan(metodo.getBody(), null);
        return complexidade.get();
    }
}
