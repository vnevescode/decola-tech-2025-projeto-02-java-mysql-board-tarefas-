package br.com.dio.decola.tech._5.board.tarefas.service;


import br.com.dio.decola.tech._5.board.tarefas.dto.BlockReportDTO;
import br.com.dio.decola.tech._5.board.tarefas.dto.ColumnTimeReportDTO;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class PdfExportService {

    public void exportTimeReportToPdf(List<ColumnTimeReportDTO> report, Path filePath) throws IOException {
        var document = new Document();
        try (var out = Files.newOutputStream(filePath)) {
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph("Relatório de Tempo em Colunas\n\n"));

            if (report.isEmpty()) {
                document.add(new Paragraph("Nenhuma movimentação encontrada."));
            } else {
                for (var item : report) {
                    var line = String.format(
                            "Card %d (%s)\nColuna: %s\nEntrou em: %s\nSaiu em: %s\nDuração: %d seg\n\n",
                            item.cardId(),
                            item.cardTitle(),
                            item.columnName(),
                            formatOffsetDateTime(item.enteredAt()),
                            formatOffsetDateTime(item.exitedAt()),
                            item.durationInSeconds()
                    );
                    document.add(new Paragraph(line));
                }
            }
        } catch (DocumentException e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        } finally {
            document.close();
        }
    }

    public void exportBlockReportToPdf(List<BlockReportDTO> blocks, Path filePath) throws IOException {
        var document = new Document();
        try (var out = Files.newOutputStream(filePath)) {
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph("Relatório de Bloqueios\n\n"));

            if (blocks.isEmpty()) {
                document.add(new Paragraph("Nenhum bloqueio encontrado."));
            } else {
                for (var b : blocks) {
                    var line = String.format(
                            "Card %d (%s)\nBloqueado em: %s\nDesbloqueado em: %s\nMotivo Bloqueio: %s\nMotivo Desbloqueio: %s\nDuração Bloqueio: %d seg\n\n",
                            b.cardId(),
                            b.cardTitle(),
                            formatOffsetDateTime(b.blockedAt()),
                            formatOffsetDateTime(b.unblockedAt()),
                            b.blockReason(),
                            b.unblockReason(),
                            b.durationInSeconds()
                    );
                    document.add(new Paragraph(line));
                }
            }
        } catch (DocumentException e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        } finally {
            document.close();
        }
    }

    private String formatOffsetDateTime(java.time.OffsetDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
