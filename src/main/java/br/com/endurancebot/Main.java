package br.com.endurancebot;

import br.com.endurancebot.model.Corrida;
import br.com.endurancebot.model.Disponibilidade;
import br.com.endurancebot.repository.DisponibilidadeRepository;
import br.com.endurancebot.service.ConversorFusoService;
import br.com.endurancebot.service.DisponibilidadeService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

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

        System.out.println("=== Cadastro da corrida pelo admin ===");

        System.out.print("Nome da corrida: ");
        String nomeCorrida = scanner.nextLine();

        System.out.print("Data da corrida (dd/MM/yyyy): ");
        LocalDate dataCorrida = LocalDate.parse(
                scanner.nextLine(),
                formatoData
        );

        System.out.print("Horário de início da corrida (HH:mm): ");
        LocalTime horarioCorrida = LocalTime.parse(
                scanner.nextLine(),
                formatoHorario
        );

        System.out.print("Duração da corrida em horas: ");
        long duracaoHoras = Long.parseLong(scanner.nextLine());

        System.out.print(
                "Fuso oficial da corrida (exemplo: Europe/London): "
        );
        String fusoOficial = scanner.nextLine();

        ZonedDateTime inicioCorridaLocal =
                ZonedDateTime.of(
                        dataCorrida,
                        horarioCorrida,
                        ZoneId.of(fusoOficial)
                );

        Corrida corrida = new Corrida(
                UUID.randomUUID().toString(),
                nomeCorrida,
                inicioCorridaLocal.toInstant(),
                inicioCorridaLocal.plusHours(duracaoHoras).toInstant(),
                fusoOficial
        );

        System.out.println();
        System.out.println("Corrida criada com sucesso.");
        System.out.println("Nome: " + corrida.getNome());
        System.out.println(
                "Início oficial: "
                        + corrida.getInicio()
                        .atZone(ZoneId.of(corrida.getFusoOficial()))
                        .format(formatoSaida)
        );

        boolean continuar = true;

        while (continuar) {
            try {
                System.out.println();
                System.out.println("=== Disponibilidade do piloto ===");

                System.out.print("Nome do piloto: ");
                String piloto = scanner.nextLine();

                System.out.print("Disponível a partir de (HH:mm): ");
                LocalTime horarioInicial = LocalTime.parse(
                        scanner.nextLine(),
                        formatoHorario
                );

                System.out.print("Disponível até (HH:mm): ");
                LocalTime horarioFinal = LocalTime.parse(
                        scanner.nextLine(),
                        formatoHorario
                );

                System.out.print(
                        "Fuso do piloto (exemplo: America/Sao_Paulo): "
                );
                String fuso = scanner.nextLine();

                disponibilidadeService.registrarDisponibilidade(
                        corrida,
                        piloto,
                        horarioInicial,
                        horarioFinal,
                        fuso
                );

                System.out.println("Disponibilidade registrada.");

                System.out.print(
                        "Cadastrar outro piloto? (s/n): "
                );

                continuar = scanner.nextLine()
                        .equalsIgnoreCase("s");

            } catch (Exception exception) {
                System.out.println(
                        "Erro: " + exception.getMessage()
                );

                System.out.print(
                        "Tentar novamente? (s/n): "
                );

                continuar = scanner.nextLine()
                        .equalsIgnoreCase("s");
            }
        }

        List<Disponibilidade> disponibilidades =
                disponibilidadeService.listarDisponibilidades();

        System.out.println();
        System.out.println("=== Resumo para os admins ===");

        for (Disponibilidade disponibilidade : disponibilidades) {
            ZonedDateTime inicioUtc =
                    disponibilidade.getInicio()
                            .atZone(ZoneId.of("UTC"));

            ZonedDateTime fimUtc =
                    disponibilidade.getFim()
                            .atZone(ZoneId.of("UTC"));

            Duration duracao =
                    Duration.between(
                            disponibilidade.getInicio(),
                            disponibilidade.getFim()
                    );

            System.out.println();
            System.out.println(
                    "Piloto: " + disponibilidade.getPiloto()
            );
            System.out.println(
                    "Disponível em UTC: "
                            + inicioUtc.format(formatoSaida)
                            + " até "
                            + fimUtc.format(formatoSaida)
            );
            System.out.println(
                    "Total disponível: "
                            + duracao.toHours()
                            + " horas"
            );
        }

        scanner.close();
    }
}
