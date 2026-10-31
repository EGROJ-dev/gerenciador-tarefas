package com.exemplo.tarefas.model;

import java.time.LocalDate;

public class Tarefa {

    private final int id;
    private String titulo;
    private Prioridade prioridade;
    private final LocalDate criadaEm;
    private LocalDate prazo; // pode ser null
    private boolean concluida;

    public Tarefa(int id, String titulo, Prioridade prioridade,
                  LocalDate criadaEm, LocalDate prazo, boolean concluida) {
        this.id = id;
        this.titulo = titulo;
        this.prioridade = prioridade;
        this.criadaEm = criadaEm;
        this.prazo = prazo;
        this.concluida = concluida;
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public Prioridade getPrioridade() { return prioridade; }
    public LocalDate getCriadaEm() { return criadaEm; }
    public LocalDate getPrazo() { return prazo; }
    public boolean isConcluida() { return concluida; }

    public void concluir() { this.concluida = true; }

    public void editar(String titulo, Prioridade prioridade, LocalDate prazo) {
        this.titulo = titulo;
        this.prioridade = prioridade;
        this.prazo = prazo;
    }

    public boolean estaAtrasada(LocalDate hoje) {
        return !concluida && prazo != null && prazo.isBefore(hoje);
    }
}
