package br.com.endurancebot.service;

import br.com.endurancebot.model.Disponibilidade;
import br.com.endurancebot.repository.DisponibilidadeRepository;

import java.time.LocalDate;
import java.time.LocalTime;
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
            String piloto,
            LocalDate data,
            LocalTime horarioInicial,
            LocalTime horarioFinal,
            String fuso
    ) {
        Disponibilidade disponibilidade =
                conversorFusoService.criarDisponibilidade(
                        piloto,
                        data,
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

    public Disponibilidade buscarPorPiloto(String piloto) {
        return disponibilidadeRepository.buscarPorPiloto(piloto);
    }
}
