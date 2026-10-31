package com.exemplo.tarefas.repository;

import com.exemplo.tarefas.model.Tarefa;
import java.util.List;

public interface TarefaRepository {
    List<Tarefa> carregar();
    void gravar(List<Tarefa> tarefas);
}
