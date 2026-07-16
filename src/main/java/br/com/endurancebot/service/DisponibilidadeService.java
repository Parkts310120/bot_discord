package br.com.endurancebot.service;

import br.com.endurancebot.model.Corrida;
import br.com.endurancebot.model.Disponibilidade;
import br.com.endurancebot.repository.DisponibilidadeRepository;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class DisponibilidadeService {

    private final ConversorFusoService conversorFusoService;
    private final DisponibilidadeRepository disponibilidadeRepository;

    public DisponibilidadeService(
            ConversorFusoService conversorFusoService,
            DisponibilidadeRepository disponibilidadeRepository
    ) {
        this.conversorFusoService = conversorFusoService;
        this.disponibilidadeRepository = disponibilidadeRepository;
    }

    public Disponibilidade registrarDisponibilidade(
            Corrida corrida,
            String piloto,
            LocalTime horarioInicial,
            LocalTime horarioFinal,
            String fuso
    ) {
        ZonedDateTime inicioCorridaNoFusoDoPiloto =
                corrida.getInicio().atZone(ZoneId.of(fuso));

        Disponibilidade disponibilidade =
                conversorFusoService.criarDisponibilidade(
                        corrida.getId(),
                        piloto,
                        inicioCorridaNoFusoDoPiloto.toLocalDate(),
                        horarioInicial,
                        horarioFinal,
                        fuso
                );

        disponibilidadeRepository.salvar(disponibilidade);

        return disponibilidade;
    }

    public List<Disponibilidade> listarDisponibilidades() {
        return disponibilidadeRepository.listarTodos();
    }
}
