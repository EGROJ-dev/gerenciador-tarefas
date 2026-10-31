package com.exemplo.tarefas.service;

import com.exemplo.tarefas.model.Prioridade;
import com.exemplo.tarefas.model.Tarefa;
import com.exemplo.tarefas.repository.TarefaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TarefaServiceTest {

    /** Repositório falso, só em memória. */
    static class RepositorioMemoria implements TarefaRepository {
        List<Tarefa> dados = new ArrayList<>();
        @Override public List<Tarefa> carregar() { return new ArrayList<>(dados); }
        @Override public void gravar(List<Tarefa> tarefas) { dados = new ArrayList<>(tarefas); }
    }

    private static final LocalDate HOJE = LocalDate.of(2026, 10, 30);

    private RepositorioMemoria repo;
    private TarefaService service;

    @BeforeEach
    void preparar() {
        Clock relogio = Clock.fixed(Instant.parse("2026-10-30T12:00:00Z"), ZoneOffset.UTC);
        repo = new RepositorioMemoria();
        service = new TarefaService(repo, relogio);
    }

    @Test
    void adicionaTarefaEGeraIdsSequenciais() {
        Tarefa a = service.adicionar("Estudar Java", Prioridade.ALTA, null);
        Tarefa b = service.adicionar("Lavar louça", Prioridade.BAIXA, null);
        assertEquals(1, a.getId());
        assertEquals(2, b.getId());
        assertEquals(2, repo.dados.size());
    }

    @Test
    void rejeitaTituloVazio() {
        assertThrows(IllegalArgumentException.class,
                () -> service.adicionar("   ", Prioridade.MEDIA, null));
    }

    @Test
    void rejeitaPrazoNoPassado() {
        assertThrows(IllegalArgumentException.class,
                () -> service.adicionar("Algo", Prioridade.MEDIA, HOJE.minusDays(1)));
    }

    @Test
    void concluirMarcaTarefaEPersiste() {
        Tarefa t = service.adicionar("Pagar conta", Prioridade.ALTA, null);
        service.concluir(t.getId());
        assertTrue(repo.dados.get(0).isConcluida());
    }

    @Test
    void removerTarefaInexistenteLancaExcecao() {
        assertThrows(TarefaNaoEncontradaException.class, () -> service.remover(99));
    }

    @Test
    void listarOrdenaPendentesPrimeiroEPorPrioridade() {
        Tarefa baixa = service.adicionar("baixa", Prioridade.BAIXA, null);
        Tarefa alta = service.adicionar("alta", Prioridade.ALTA, null);
        Tarefa feita = service.adicionar("feita", Prioridade.ALTA, null);
        service.concluir(feita.getId());

        List<Tarefa> lista = service.listar();
        assertEquals(alta.getId(), lista.get(0).getId());
        assertEquals(baixa.getId(), lista.get(1).getId());
        assertEquals(feita.getId(), lista.get(2).getId());
    }

    @Test
    void detectaTarefaAtrasada() {
        Tarefa t = new Tarefa(1, "velha", Prioridade.MEDIA, HOJE.minusDays(10), HOJE.minusDays(2), false);
        repo.dados.add(t);
        TarefaService s = new TarefaService(repo, Clock.fixed(Instant.parse("2026-10-30T12:00:00Z"), ZoneOffset.UTC));
        assertEquals(1, s.listarAtrasadas().size());
    }
}
