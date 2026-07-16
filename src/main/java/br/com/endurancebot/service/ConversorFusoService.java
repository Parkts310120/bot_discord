package br.com.endurancebot.service;

import br.com.endurancebot.model.Disponibilidade;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ConversorFusoService {

    public Disponibilidade criarDisponibilidade(
            String piloto,
            LocalDate data,
            LocalTime horarioInicial,
            LocalTime horarioFinal,
            String fuso
    ) {
        ZoneId zonaPiloto = ZoneId.of(fuso);

        ZonedDateTime inicioLocal =
                ZonedDateTime.of(data, horarioInicial, zonaPiloto);

        ZonedDateTime fimLocal =
                ZonedDateTime.of(data, horarioFinal, zonaPiloto);

        if (!horarioFinal.isAfter(horarioInicial)) {
            fimLocal = fimLocal.plusDays(1);
        }

        return new Disponibilidade(
                piloto,
                inicioLocal.toInstant(),
                fimLocal.toInstant(),
                fuso
        );
    }
}
