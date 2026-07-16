package br.com.endurancebot.model;

import java.time.Instant;

public class Disponibilidade {

    private String piloto;
    private Instant inicio;
    private Instant fim;
    private String fusoOriginal;

    public Disponibilidade(
            String piloto,
            Instant inicio,
            Instant fim,
            String fusoOriginal
    ) {
        this.piloto = piloto;
        this.inicio = inicio;
        this.fim = fim;
        this.fusoOriginal = fusoOriginal;
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
