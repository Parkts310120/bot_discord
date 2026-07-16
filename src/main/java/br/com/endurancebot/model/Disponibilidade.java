package br.com.endurancebot.model;

import java.time.Instant;

public class Disponibilidade {

    private final String corridaId;
    private final String piloto;
    private final Instant inicio;
    private final Instant fim;
    private final String fusoOriginal;

    public Disponibilidade(
            String corridaId,
            String piloto,
            Instant inicio,
            Instant fim,
            String fusoOriginal
    ) {
        this.corridaId = corridaId;
        this.piloto = piloto;
        this.inicio = inicio;
        this.fim = fim;
        this.fusoOriginal = fusoOriginal;
    }

    public String getCorridaId() {
        return corridaId;
    }

    public String getPiloto() {
        return piloto;
    }

    public Instant getInicio() {
        return inicio;
    }

    public Instant getFim() {
        return fim;
    }

    public String getFusoOriginal() {
        return fusoOriginal;
    }
}
