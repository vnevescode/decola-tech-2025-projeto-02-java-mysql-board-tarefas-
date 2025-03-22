package br.com.dio.decola.tech._5.board.tarefas.service;

import br.com.dio.decola.tech._5.board.tarefas.dto.BlockReportDTO;
import br.com.dio.decola.tech._5.board.tarefas.dto.ColumnTimeReportDTO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CsvExportService {

    public void exportTimeReportToCsv(List<ColumnTimeReportDTO> report, Path filePath) throws IOException {
        try (Writer writer = Files.newBufferedWriter(filePath);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                     .withHeader("Card ID", "Card Title", "Column Name", "Entered At", "Exited At", "Duration (seconds)"))) {

            for (var item : report) {
                csvPrinter.printRecord(
                        item.cardId(),
                        item.cardTitle(),
                        item.columnName(),
                        formatOffsetDateTime(item.enteredAt()),
                        formatOffsetDateTime(item.exitedAt()),
                        item.durationInSeconds()
                );
            }
        }
    }

    public void exportBlockReportToCsv(List<BlockReportDTO> blocks, Path filePath) throws IOException {
        try (Writer writer = Files.newBufferedWriter(filePath);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                     .withHeader("Card ID", "Card Title", "Blocked At", "Unblocked At", "Block Reason", "Unblock Reason", "Duration (seconds)"))) {

            for (var b : blocks) {
                csvPrinter.printRecord(
                        b.cardId(),
                        b.cardTitle(),
                        formatOffsetDateTime(b.blockedAt()),
                        formatOffsetDateTime(b.unblockedAt()),
                        b.blockReason(),
                        b.unblockReason(),
                        b.durationInSeconds()
                );
            }
        }
    }

    private String formatOffsetDateTime(java.time.OffsetDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
