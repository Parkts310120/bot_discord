package br.com.endurancebot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Endurance Stint Bot ===");

        System.out.print("Nome do piloto: ");
        String nomePiloto = scanner.nextLine();

        System.out.print("Data da corrida (dd/MM/yyyy): ");
        String dataInformada = scanner.nextLine();

        System.out.print("Horário inicial (HH:mm): ");
        String horarioInicialInformado = scanner.nextLine();

        System.out.print("Horário final (HH:mm): ");
        String horarioFinalInformado = scanner.nextLine();

        System.out.print("Fuso horário (exemplo: America/Sao_Paulo): ");
        String fusoInformado = scanner.nextLine();

        try {
            DateTimeFormatter formatoData =
                    DateTimeFormatter.ofPattern("dd/MM/yyyy");

            DateTimeFormatter formatoHorario =
                    DateTimeFormatter.ofPattern("HH:mm");

            LocalDate data =
                    LocalDate.parse(dataInformada, formatoData);

            LocalTime horarioInicial =
                    LocalTime.parse(horarioInicialInformado, formatoHorario);

            LocalTime horarioFinal =
                    LocalTime.parse(horarioFinalInformado, formatoHorario);

            ZoneId fusoPiloto = ZoneId.of(fusoInformado);

            ZonedDateTime inicioPiloto =
                    ZonedDateTime.of(data, horarioInicial, fusoPiloto);

            ZonedDateTime fimPiloto =
                    ZonedDateTime.of(data, horarioFinal, fusoPiloto);

            if (!horarioFinal.isAfter(horarioInicial)) {
                fimPiloto = fimPiloto.plusDays(1);
            }

            ZonedDateTime inicioUtc =
                    inicioPiloto.withZoneSameInstant(ZoneId.of("UTC"));

            ZonedDateTime fimUtc =
                    fimPiloto.withZoneSameInstant(ZoneId.of("UTC"));

            DateTimeFormatter formatoSaida =
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm z");

            System.out.println();
            System.out.println("=== Disponibilidade registrada ===");
            System.out.println("Piloto: " + nomePiloto);
            System.out.println(
                    "Horário original: "
                            + inicioPiloto.format(formatoSaida)
                            + " até "
                            + fimPiloto.format(formatoSaida)
            );
            System.out.println(
                    "Horário UTC: "
                            + inicioUtc.format(formatoSaida)
                            + " até "
                            + fimUtc.format(formatoSaida)
            );

        } catch (Exception exception) {
            System.out.println();
            System.out.println("Não foi possível registrar a disponibilidade.");
            System.out.println("Motivo: " + exception.getMessage());
        }

        scanner.close();
    }
}
