package br.com.endurancebot.repository;

import br.com.endurancebot.model.Disponibilidade;

import java.util.ArrayList;
import java.util.List;

public class DisponibilidadeRepository {

    private final List<Disponibilidade> disponibilidades;

    public DisponibilidadeRepository() {
        this.disponibilidades = new ArrayList<>();
    }

    public void salvar(Disponibilidade disponibilidade) {
        disponibilidades.add(disponibilidade);
    }

    public List<Disponibilidade> listarTodos() {
        return new ArrayList<>(disponibilidades);
    }

    public Disponibilidade buscarPorPiloto(String piloto) {
        for (Disponibilidade disponibilidade : disponibilidades) {
            if (disponibilidade.getPiloto().equalsIgnoreCase(piloto)) {
                return disponibilidade;
            }
        }

        return null;
    }
}
