package br.com.endurancebot;

import br.com.endurancebot.model.Disponibilidade;
import br.com.endurancebot.repository.DisponibilidadeRepository;
import br.com.endurancebot.service.ConversorFusoService;
import br.com.endurancebot.service.DisponibilidadeService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        ConversorFusoService conversorFusoService =
                new ConversorFusoService();

        DisponibilidadeRepository disponibilidadeRepository =
                new DisponibilidadeRepository();

        DisponibilidadeService disponibilidadeService =
                new DisponibilidadeService(
                        conversorFusoService,
                        disponibilidadeRepository
                );

        DateTimeFormatter formatoData =
                DateTimeFormatter.ofPattern("dd/MM/yyyy");

        DateTimeFormatter formatoHorario =
                DateTimeFormatter.ofPattern("HH:mm");

        DateTimeFormatter formatoSaida =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm z");

        System.out.println("=== Endurance Stint Bot ===");

        boolean continuar = true;

        while (continuar) {
            try {
                System.out.println();
                System.out.print("Nome do piloto: ");
                String piloto = scanner.nextLine();

                System.out.print("Data da corrida (dd/MM/yyyy): ");
                LocalDate data = LocalDate.parse(
                        scanner.nextLine(),
                        formatoData
                );

                System.out.print("Horário inicial (HH:mm): ");
                LocalTime horarioInicial = LocalTime.parse(
                        scanner.nextLine(),
                        formatoHorario
                );

                System.out.print("Horário final (HH:mm): ");
                LocalTime horarioFinal = LocalTime.parse(
                        scanner.nextLine(),
                        formatoHorario
                );

                System.out.print(
                        "Fuso horário (exemplo: America/Sao_Paulo): "
                );
                String fuso = scanner.nextLine();

                Disponibilidade disponibilidade =
                        disponibilidadeService.registrarDisponibilidade(
                                piloto,
                                data,
                                horarioInicial,
                                horarioFinal,
                                fuso
                        );

                System.out.println();
                System.out.println("Disponibilidade registrada para "
                        + disponibilidade.getPiloto());

                System.out.print(
                        "Deseja cadastrar outro piloto? (s/n): "
                );

                String resposta = scanner.nextLine();

                continuar = resposta.equalsIgnoreCase("s");

            } catch (Exception exception) {
                System.out.println();
                System.out.println(
                        "Não foi possível registrar a disponibilidade."
                );
                System.out.println(
                        "Motivo: " + exception.getMessage()
                );

                System.out.print(
                        "Deseja tentar novamente? (s/n): "
                );

                String resposta = scanner.nextLine();

                continuar = resposta.equalsIgnoreCase("s");
            }
        }

        List<Disponibilidade> disponibilidades =
                disponibilidadeService.listarDisponibilidades();

        System.out.println();
        System.out.println("=== Disponibilidades cadastradas ===");

        if (disponibilidades.isEmpty()) {
            System.out.println("Nenhuma disponibilidade cadastrada.");
        } else {
            for (Disponibilidade disponibilidade : disponibilidades) {
                ZonedDateTime inicioUtc =
                        disponibilidade.getInicio()
                                .atZone(ZoneId.of("UTC"));

                ZonedDateTime fimUtc =
                        disponibilidade.getFim()
                                .atZone(ZoneId.of("UTC"));

                System.out.println();
                System.out.println(
                        "Piloto: " + disponibilidade.getPiloto()
                );
                System.out.println(
                        "Fuso original: "
                                + disponibilidade.getFusoOriginal()
                );
                System.out.println(
                        "Horário UTC: "
                                + inicioUtc.format(formatoSaida)
                                + " até "
                                + fimUtc.format(formatoSaida)
                );
            }
        }

        scanner.close();
    }
}
