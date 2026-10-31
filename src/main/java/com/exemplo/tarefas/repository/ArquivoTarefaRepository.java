package com.exemplo.tarefas.repository;

import com.exemplo.tarefas.model.Prioridade;
import com.exemplo.tarefas.model.Tarefa;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Persiste as tarefas em um arquivo de texto separado por tabulação (TSV). */
public class ArquivoTarefaRepository implements TarefaRepository {

    private final Path arquivo;

    public ArquivoTarefaRepository(Path arquivo) {
        this.arquivo = arquivo;
    }

    @Override
    public List<Tarefa> carregar() {
        if (!Files.exists(arquivo)) {
            return new ArrayList<>();
        }
        try {
            List<Tarefa> tarefas = new ArrayList<>();
            for (String linha : Files.readAllLines(arquivo, StandardCharsets.UTF_8)) {
                if (!linha.isBlank()) {
                    tarefas.add(deLinha(linha));
                }
            }
            return tarefas;
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao ler " + arquivo, e);
        }
    }

    @Override
    public void gravar(List<Tarefa> tarefas) {
        List<String> linhas = tarefas.stream().map(ArquivoTarefaRepository::paraLinha).toList();
        try {
            Files.write(arquivo, linhas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao gravar " + arquivo, e);
        }
    }

    private static String paraLinha(Tarefa t) {
        String titulo = t.getTitulo().replaceAll("[\\t\\r\\n]+", " ");
        String prazo = t.getPrazo() == null ? "" : t.getPrazo().toString();
        return String.join("\t",
                String.valueOf(t.getId()), titulo, t.getPrioridade().name(),
                String.valueOf(t.isConcluida()), t.getCriadaEm().toString(), prazo);
    }

    private static Tarefa deLinha(String linha) {
        String[] c = linha.split("\t", -1);
        LocalDate prazo = c[5].isEmpty() ? null : LocalDate.parse(c[5]);
        return new Tarefa(Integer.parseInt(c[0]), c[1], Prioridade.valueOf(c[2]),
                LocalDate.parse(c[4]), prazo, Boolean.parseBoolean(c[3]));
    }
}
