package br.com.dio.decola.tech._5.board.tarefas.dto;

import java.time.OffsetDateTime;

public record BlockReportDTO(
        Long cardId,
        String cardTitle,
        OffsetDateTime blockedAt,
        OffsetDateTime unblockedAt,
        String blockReason,
        String unblockReason,
        long durationInSeconds
) {}
