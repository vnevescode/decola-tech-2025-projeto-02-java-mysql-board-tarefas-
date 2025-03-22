package br.com.dio.decola.tech._5.board.tarefas.ui;

import br.com.dio.decola.tech._5.board.tarefas.dto.BoardColumnInfoDTO;
import br.com.dio.decola.tech._5.board.tarefas.persistence.entity.BoardColumnEntity;
import br.com.dio.decola.tech._5.board.tarefas.persistence.entity.BoardEntity;
import br.com.dio.decola.tech._5.board.tarefas.persistence.entity.CardEntity;
import br.com.dio.decola.tech._5.board.tarefas.service.*;
import lombok.AllArgsConstructor;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Scanner;

import static br.com.dio.decola.tech._5.board.tarefas.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class BoardMenu {

    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");

    private final BoardEntity entity;

    public void execute() {
        try {
            System.out.printf("Bem vindo ao board %s, selecione a operação desejada\n", entity.getId());
            var option = -1;
            while (option != 9) {
                System.out.println("1 - Criar um card");
                System.out.println("2 - Mover um card");
                System.out.println("3 - Bloquear um card");
                System.out.println("4 - Desbloquear um card");
                System.out.println("5 - Cancelar um card");
                System.out.println("6 - Ver board");
                System.out.println("7 - Ver coluna com cards");
                System.out.println("8 - Ver card");
                System.out.println("9 - Relatório de Tempo em Colunas (console)");
                System.out.println("10 - Relatório de Bloqueios (console)");
                System.out.println("11 - Exportar Relatório de Tempo em Colunas para CSV");
                System.out.println("12 - Exportar Relatório de Bloqueios para CSV");
                System.out.println("13 - Exportar Relatório de Tempo em Colunas para PDF");
                System.out.println("14 - Exportar Relatório de Bloqueios para PDF");
                System.out.println("20 - Voltar para o menu anterior");
                System.out.println("21 - Sair");
                option = scanner.nextInt();
                switch (option) {
                    case 1 -> createCard();
                    case 2 -> moveCardToNextColumn();
                    case 3 -> blockCard();
                    case 4 -> unblockCard();
                    case 5 -> cancelCard();
                    case 6 -> showBoard();
                    case 7 -> showColumn();
                    case 8 -> showCard();
                    case 9 -> showTimeReport();
                    case 10 -> showBlockReport();
                    case 11 -> exportTimeReportCsv();
                    case 12 -> exportBlockReportCsv();
                    case 13 -> exportTimeReportPdf();
                    case 14 -> exportBlockReportPdf();
                    case 20 -> System.out.println("Voltando para o menu anterior");
                    case 21 -> System.exit(0);
                    default -> System.out.println("Opção inválida, informe uma opção do menu");
                }
            }
        }catch (SQLException ex){
            ex.printStackTrace();
            System.exit(0);
        }
    }

    private void createCard() throws SQLException{
        var card = new CardEntity();
        System.out.println("Informe o título do card");
        card.setTitle(scanner.next());
        System.out.println("Informe a descrição do card");
        card.setDescription(scanner.next());
        card.setBoardColumn(entity.getInitialColumn());
        try(var connection = getConnection()){
            new CardService(connection).create(card);
        }
    }

    private void moveCardToNextColumn() throws SQLException {
        System.out.println("Informe o id do card que deseja mover para a próxima coluna");
        var cardId = scanner.nextLong();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()){
            new CardService(connection).moveToNextColumn(cardId, boardColumnsInfo);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void blockCard() throws SQLException {
        System.out.println("Informe o id do card que será bloqueado");
        var cardId = scanner.nextLong();
        System.out.println("Informe o motivo do bloqueio do card");
        var reason = scanner.next();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()){
            new CardService(connection).block(cardId, reason, boardColumnsInfo);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void unblockCard() throws SQLException {
        System.out.println("Informe o id do card que será desbloqueado");
        var cardId = scanner.nextLong();
        System.out.println("Informe o motivo do desbloqueio do card");
        var reason = scanner.next();
        try(var connection = getConnection()){
            new CardService(connection).unblock(cardId, reason);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void cancelCard() throws SQLException {
        System.out.println("Informe o id do card que deseja mover para a coluna de cancelamento");
        var cardId = scanner.nextLong();
        var cancelColumn = entity.getCancelColumn();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()){
            new CardService(connection).cancel(cardId, cancelColumn.getId(), boardColumnsInfo);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void showBoard() throws SQLException {
        try(var connection = getConnection()){
            var optional = new BoardQueryService(connection).showBoardDetails(entity.getId());
            optional.ifPresent(b -> {
                System.out.printf("Board [%s,%s]\n", b.id(), b.name());
                b.columns().forEach(c ->
                        System.out.printf("Coluna [%s] tipo: [%s] tem %s cards\n", c.name(), c.kind(), c.cardsAmount())
                );
            });
        }
    }

    private void showColumn() throws SQLException {
        var columnsIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumnId = -1L;
        while (!columnsIds.contains(selectedColumnId)){
            System.out.printf("Escolha uma coluna do board %s pelo id\n", entity.getName());
            entity.getBoardColumns().forEach(c -> System.out.printf("%s - %s [%s]\n", c.getId(), c.getName(), c.getKind()));
            selectedColumnId = scanner.nextLong();
        }
        try(var connection = getConnection()){
            var column = new BoardColumnQueryService(connection).findById(selectedColumnId);
            column.ifPresent(co -> {
                System.out.printf("Coluna %s tipo %s\n", co.getName(), co.getKind());
                co.getCards().forEach(ca -> System.out.printf("Card %s - %s\nDescrição: %s",
                        ca.getId(), ca.getTitle(), ca.getDescription()));
            });
        }
    }

    private void showCard() throws SQLException {
        System.out.println("Informe o id do card que deseja visualizar");
        var selectedCardId = scanner.nextLong();
        try(var connection  = getConnection()){
            new CardQueryService(connection).findById(selectedCardId)
                    .ifPresentOrElse(
                            c -> {
                                System.out.printf("Card %s - %s.\n", c.id(), c.title());
                                System.out.printf("Descrição: %s\n", c.description());
                                System.out.println(c.blocked() ?
                                        "Está bloqueado. Motivo: " + c.blockReason() :
                                        "Não está bloqueado");
                                System.out.printf("Já foi bloqueado %s vezes\n", c.blocksAmount());
                                System.out.printf("Está no momento na coluna %s - %s\n", c.columnId(), c.columnName());
                            },
                            () -> System.out.printf("Não existe um card com o id %s\n", selectedCardId));
        }
    }

    private void showTimeReport() throws SQLException {
        try (var connection = getConnection()) {
            var service = new ReportService(connection);
            var list = service.getTimeReportForBoard(entity.getId());
            if (list.isEmpty()) {
                System.out.println("Não há histórico de movimentação para este board.");
                return;
            }
            // Exemplo simples: imprimir linha a linha
            System.out.printf("Relatório de tempo nas colunas do board %s\n", entity.getName());
            for (var item : list) {
                System.out.printf(
                        "Card %d (%s) - Coluna: %s - Entrou em: %s - Saiu em: %s - Durou: %d seg\n",
                        item.cardId(),
                        item.cardTitle(),
                        item.columnName(),
                        item.enteredAt(),
                        item.exitedAt(),
                        item.durationInSeconds()
                );
            }
        }
    }

    private void showBlockReport() throws SQLException {
        try (var connection = getConnection()) {
            var service = new ReportService(connection);
            var blocks = service.getBlockReportForBoard(entity.getId());
            if (blocks.isEmpty()) {
                System.out.println("Nenhum bloqueio encontrado para este board.");
                return;
            }
            System.out.printf("Relatório de bloqueios do board %s\n", entity.getName());
            for (var b : blocks) {
                System.out.printf(
                        "Card %d (%s)\n - Bloqueado em: %s\n - Desbloqueado em: %s\n - Motivo bloqueio: %s\n - Motivo desbloqueio: %s\n - Ficou bloqueado por: %d seg\n\n",
                        b.cardId(),
                        b.cardTitle(),
                        b.blockedAt(),
                        b.unblockedAt(),
                        b.blockReason(),
                        b.unblockReason(),
                        b.durationInSeconds()
                );
            }
        }
    }

    private void exportTimeReportCsv() throws SQLException {
        try (var connection = getConnection()) {
            var reportService = new ReportService(connection);
            var csvExport = new CsvExportService();

            // 1) Obtemos os dados
            var list = reportService.getTimeReportForBoard(entity.getId());

            // 2) Pedimos ao usuário para informar o caminho do arquivo CSV
            System.out.println("Informe o caminho onde salvar o CSV (ex: C:/temp/time_report.csv):");
            var filePath = scanner.next().trim();

            // 3) Exportamos
            csvExport.exportTimeReportToCsv(list, Path.of(filePath));
            System.out.println("Relatório de tempo exportado para CSV com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao exportar CSV: " + e.getMessage());
        }
    }

    private void exportBlockReportCsv() throws SQLException {
        try (var connection = getConnection()) {
            var reportService = new ReportService(connection);
            var csvExport = new CsvExportService();

            var blocks = reportService.getBlockReportForBoard(entity.getId());

            System.out.println("Informe o caminho onde salvar o CSV (ex: C:/temp/block_report.csv):");
            var filePath = scanner.next().trim();

            csvExport.exportBlockReportToCsv(blocks, Path.of(filePath));
            System.out.println("Relatório de bloqueios exportado para CSV com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao exportar CSV: " + e.getMessage());
        }
    }

    private void exportTimeReportPdf() throws SQLException {
        try (var connection = getConnection()) {
            var reportService = new ReportService(connection);
            var pdfExport = new PdfExportService();

            var list = reportService.getTimeReportForBoard(entity.getId());

            System.out.println("Informe o caminho onde salvar o PDF (ex: C:/temp/time_report.pdf):");
            var filePath = scanner.next().trim();

            pdfExport.exportTimeReportToPdf(list, Path.of(filePath));
            System.out.println("Relatório de tempo exportado para PDF com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao exportar PDF: " + e.getMessage());
        }
    }

    private void exportBlockReportPdf() throws SQLException {
        try (var connection = getConnection()) {
            var reportService = new ReportService(connection);
            var pdfExport = new PdfExportService();

            var blocks = reportService.getBlockReportForBoard(entity.getId());

            System.out.println("Informe o caminho onde salvar o PDF (ex: C:/temp/block_report.pdf):");
            var filePath = scanner.next().trim();

            pdfExport.exportBlockReportToPdf(blocks, Path.of(filePath));
            System.out.println("Relatório de bloqueios exportado para PDF com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao exportar PDF: " + e.getMessage());
        }
    }

}
