# 📝 Gerenciador de Tarefas (CLI)

![CI](https://github.com/EGROJ-dev/gerenciador-tarefas/actions/workflows/ci.yml/badge.svg)

Aplicação de linha de comando em **Java 17** para gerenciar tarefas, com prioridades, prazos e persistência em arquivo. Feita sem frameworks, só Java puro + JUnit 5, como projeto de estudo de arquitetura em camadas.

## Funcionalidades

- Adicionar tarefas com título, prioridade (baixa/média/alta) e prazo opcional
- Listar tarefas (pendentes primeiro, ordenadas por prioridade)
- Listar tarefas atrasadas
- Concluir e remover tarefas
- Dados salvos automaticamente em `~/.tarefas.tsv`

## Estrutura

```
src/main/java/com/exemplo/tarefas
├── Main.java                  # ponto de entrada
├── cli/Menu.java              # interação com o usuário
├── model/                     # Tarefa e Prioridade
├── repository/                # interface + implementação em arquivo
└── service/TarefaService.java # regras de negócio
```

O `TarefaService` depende da interface `TarefaRepository` e de um `Clock`, o que permite testar as regras sem tocar em disco e sem depender da data real.

## Como executar

Requisitos: JDK 17+ e Maven 3.8+.

```bash
mvn clean package
java -jar target/gerenciador-tarefas-1.0.0.jar
```

Para usar outro arquivo de dados:

```bash
java -jar target/gerenciador-tarefas-1.0.0.jar minhas-tarefas.tsv
```

## Testes

```bash
mvn test
```

## Ideias para evoluir

- [ ] Editar tarefas existentes
- [ ] Filtrar por prioridade
- [ ] Trocar o arquivo TSV por JSON (Jackson) ou SQLite
- [ ] Transformar em API REST com Spring Boot

## Licença

MIT
