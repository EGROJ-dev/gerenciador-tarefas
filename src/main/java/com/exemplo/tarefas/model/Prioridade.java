package com.exemplo.tarefas.model;

public enum Prioridade {
    BAIXA, MEDIA, ALTA;

    public static Prioridade de(String texto) {
        return switch (texto.trim().toLowerCase()) {
            case "1", "baixa" -> BAIXA;
            case "2", "media", "média" -> MEDIA;
            case "3", "alta" -> ALTA;
            default -> throw new IllegalArgumentException("Prioridade inválida: " + texto);
        };
    }
}
