package br.com.endurancebot.model;

import java.time.Instant;

public class Corrida {

    private final String id;
    private final String nome;
    private final Instant inicio;
    private final Instant fim;
    private final String fusoOficial;

    public Corrida(
            String id,
            String nome,
            Instant inicio,
            Instant fim,
            String fusoOficial
    ) {
        this.id = id;
        this.nome = nome;
        this.inicio = inicio;
        this.fim = fim;
        this.fusoOficial = fusoOficial;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Instant getInicio() {
        return inicio;
    }

    public Instant getFim() {
        return fim;
    }

    public String getFusoOficial() {
        return fusoOficial;
    }
}
