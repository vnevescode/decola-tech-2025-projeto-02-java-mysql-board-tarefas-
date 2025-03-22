package br.com.dio.decola.tech._5.board.tarefas.dto;

import java.time.OffsetDateTime;

public record ColumnTimeReportDTO(
        Long cardId,
        String cardTitle,
        String columnName,
        OffsetDateTime enteredAt,
        OffsetDateTime exitedAt,
        long durationInSeconds // ou Duration
) {}
