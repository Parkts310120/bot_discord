package br.com.endurancebot;

import br.com.endurancebot.model.Disponibilidade;
import br.com.endurancebot.service.ConversorFusoService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ConversorFusoService conversor = new ConversorFusoService();

        DateTimeFormatter formatoData =
                DateTimeFormatter.ofPattern("dd/MM/yyyy");

        DateTimeFormatter formatoHorario =
                DateTimeFormatter.ofPattern("HH:mm");

        DateTimeFormatter formatoSaida =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm z");

        System.out.println("=== Endurance Stint Bot ===");

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

        try {
            Disponibilidade disponibilidade =
                    conversor.criarDisponibilidade(
                            piloto,
                            data,
                            horarioInicial,
                            horarioFinal,
                            fuso
                    );

            ZonedDateTime inicioUtc =
                    disponibilidade.getInicio()
                            .atZone(ZoneId.of("UTC"));

            ZonedDateTime fimUtc =
                    disponibilidade.getFim()
                            .atZone(ZoneId.of("UTC"));

            System.out.println();
            System.out.println(
                    "=== Disponibilidade registrada ==="
            );
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

        } catch (Exception exception) {
            System.out.println();
            System.out.println(
                    "Não foi possível registrar a disponibilidade."
            );
            System.out.println(
                    "Motivo: " + exception.getMessage()
            );
        }

        scanner.close();
    }
}
