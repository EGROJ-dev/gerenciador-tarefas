package com.exemplo.tarefas.service;

import com.exemplo.tarefas.model.Prioridade;
import com.exemplo.tarefas.model.Tarefa;
import com.exemplo.tarefas.repository.TarefaRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TarefaService {

    private final TarefaRepository repositorio;
    private final Clock relogio;
    private final List<Tarefa> tarefas;

    public TarefaService(TarefaRepository repositorio, Clock relogio) {
        this.repositorio = repositorio;
        this.relogio = relogio;
        this.tarefas = new ArrayList<>(repositorio.carregar());
    }

    public Tarefa adicionar(String titulo, Prioridade prioridade, LocalDate prazo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("O título não pode ser vazio.");
        }
        LocalDate hoje = LocalDate.now(relogio);
        if (prazo != null && prazo.isBefore(hoje)) {
            throw new IllegalArgumentException("O prazo não pode estar no passado.");
        }
        Tarefa tarefa = new Tarefa(proximoId(), titulo.trim(), prioridade, hoje, prazo, false);
        tarefas.add(tarefa);
        repositorio.gravar(tarefas);
        return tarefa;
    }

    /**
     * Altera título, prioridade e prazo de uma tarefa existente.
     * Manter um prazo que já estava no passado é permitido (tarefa atrasada);
     * só é recusado escolher um prazo novo que esteja no passado.
     */
    public Tarefa editar(int id, String titulo, Prioridade prioridade, LocalDate prazo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("O título não pode ser vazio.");
        }
        Tarefa tarefa = buscar(id);
        LocalDate hoje = LocalDate.now(relogio);
        boolean prazoMudou = prazo != null && !prazo.equals(tarefa.getPrazo());
        if (prazoMudou && prazo.isBefore(hoje)) {
            throw new IllegalArgumentException("O prazo não pode estar no passado.");
        }
        tarefa.editar(titulo.trim(), prioridade, prazo);
        repositorio.gravar(tarefas);
        return tarefa;
    }

    public void concluir(int id) {
        buscar(id).concluir();
        repositorio.gravar(tarefas);
    }

    public void remover(int id) {
        tarefas.remove(buscar(id));
        repositorio.gravar(tarefas);
    }

    public Tarefa buscar(int id) {
        return tarefas.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElseThrow(() -> new TarefaNaoEncontradaException(id));
    }

    /** Pendentes primeiro, depois por prioridade (maior antes) e por id. */
    public List<Tarefa> listar() {
        return tarefas.stream()
                .sorted(Comparator.comparing(Tarefa::isConcluida)
                        .thenComparing(Tarefa::getPrioridade, Comparator.reverseOrder())
                        .thenComparing(Tarefa::getId))
                .toList();
    }

    public List<Tarefa> listarAtrasadas() {
        LocalDate hoje = LocalDate.now(relogio);
        return listar().stream().filter(t -> t.estaAtrasada(hoje)).toList();
    }

    public LocalDate hoje() {
        return LocalDate.now(relogio);
    }

    private int proximoId() {
        return tarefas.stream().mapToInt(Tarefa::getId).max().orElse(0) + 1;
    }
}
