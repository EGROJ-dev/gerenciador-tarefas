package com.exemplo.tarefas;

import com.exemplo.tarefas.cli.Menu;
import com.exemplo.tarefas.repository.ArquivoTarefaRepository;
import com.exemplo.tarefas.service.TarefaService;

import java.nio.file.Path;
import java.time.Clock;

public class Main {
    public static void main(String[] args) {
        Path arquivo = args.length > 0
                ? Path.of(args[0])
                : Path.of(System.getProperty("user.home"), ".tarefas.tsv");

        TarefaService service = new TarefaService(new ArquivoTarefaRepository(arquivo), Clock.systemDefaultZone());
        new Menu(service).executar();
    }
}
