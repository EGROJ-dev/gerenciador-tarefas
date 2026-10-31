package com.exemplo.tarefas.service;

public class TarefaNaoEncontradaException extends RuntimeException {
    public TarefaNaoEncontradaException(int id) {
        super("Tarefa #" + id + " não encontrada.");
    }
}
