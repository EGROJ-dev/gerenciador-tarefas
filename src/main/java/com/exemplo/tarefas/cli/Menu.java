package com.exemplo.tarefas.cli;

import com.exemplo.tarefas.model.Prioridade;
import com.exemplo.tarefas.model.Tarefa;
import com.exemplo.tarefas.service.TarefaService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Menu {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final TarefaService service;
    private final Scanner entrada = new Scanner(System.in);

    public Menu(TarefaService service) {
        this.service = service;
    }

    public void executar() {
        boolean rodando = true;
        while (rodando) {
            imprimirMenu();
            String opcao = entrada.nextLine().trim();
            try {
                switch (opcao) {
                    case "1" -> adicionar();
                    case "2" -> imprimirLista(service.listar());
                    case "3" -> imprimirLista(service.listarAtrasadas());
                    case "4" -> { service.concluir(lerInt("ID da tarefa: ")); System.out.println("Concluída!"); }
                    case "5" -> { service.remover(lerInt("ID da tarefa: ")); System.out.println("Removida!"); }
                    case "6" -> editar();
                    case "0" -> rodando = false;
                    default -> System.out.println("Opção inválida.");
                }
            } catch (RuntimeException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
        System.out.println("Até mais!");
    }

    private void imprimirMenu() {
        System.out.println("""

                ===== Gerenciador de Tarefas =====
                1) Adicionar tarefa
                2) Listar todas
                3) Listar atrasadas
                4) Concluir tarefa
                5) Remover tarefa
                6) Editar tarefa
                0) Sair""");
        System.out.print("> ");
    }

    private void adicionar() {
        System.out.print("Título: ");
        String titulo = entrada.nextLine();
        System.out.print("Prioridade (1-baixa, 2-média, 3-alta): ");
        Prioridade prioridade = Prioridade.de(entrada.nextLine());
        System.out.print("Prazo (dd/MM/yyyy, vazio para nenhum): ");
        String textoPrazo = entrada.nextLine().trim();
        LocalDate prazo = null;
        if (!textoPrazo.isEmpty()) {
            try {
                prazo = LocalDate.parse(textoPrazo, FORMATO);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Data inválida. Use dd/MM/yyyy.");
            }
        }
        Tarefa t = service.adicionar(titulo, prioridade, prazo);
        System.out.println("Tarefa #" + t.getId() + " criada.");
    }

    private void editar() {
        Tarefa atual = service.buscar(lerInt("ID da tarefa: "));
        System.out.println("Deixe em branco para manter o valor atual.");

        System.out.print("Título [" + atual.getTitulo() + "]: ");
        String titulo = entrada.nextLine();
        if (titulo.isBlank()) {
            titulo = atual.getTitulo();
        }

        System.out.print("Prioridade (1-baixa, 2-média, 3-alta) [" + atual.getPrioridade() + "]: ");
        String textoPrioridade = entrada.nextLine();
        Prioridade prioridade = textoPrioridade.isBlank() ? atual.getPrioridade() : Prioridade.de(textoPrioridade);

        String prazoAtual = atual.getPrazo() == null ? "nenhum" : atual.getPrazo().format(FORMATO);
        System.out.print("Prazo (dd/MM/yyyy, '-' para remover) [" + prazoAtual + "]: ");
        String textoPrazo = entrada.nextLine().trim();
        LocalDate prazo = atual.getPrazo();
        if (textoPrazo.equals("-")) {
            prazo = null;
        } else if (!textoPrazo.isEmpty()) {
            try {
                prazo = LocalDate.parse(textoPrazo, FORMATO);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Data inválida. Use dd/MM/yyyy.");
            }
        }

        service.editar(atual.getId(), titulo, prioridade, prazo);
        System.out.println("Tarefa #" + atual.getId() + " atualizada.");
    }

    private int lerInt(String mensagem) {
        System.out.print(mensagem);
        try {
            return Integer.parseInt(entrada.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Digite um número válido.");
        }
    }

    private void imprimirLista(List<Tarefa> tarefas) {
        if (tarefas.isEmpty()) {
            System.out.println("Nenhuma tarefa encontrada.");
            return;
        }
        LocalDate hoje = service.hoje();
        for (Tarefa t : tarefas) {
            String prazo = t.getPrazo() == null ? "sem prazo" : "prazo " + t.getPrazo().format(FORMATO);
            String atraso = t.estaAtrasada(hoje) ? "  [ATRASADA]" : "";
            System.out.printf("[%s] #%d %s (%s, %s)%s%n",
                    t.isConcluida() ? "x" : " ", t.getId(), t.getTitulo(),
                    t.getPrioridade(), prazo, atraso);
        }
    }
}
