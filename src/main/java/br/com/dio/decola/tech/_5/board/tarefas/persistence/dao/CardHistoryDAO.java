package br.com.dio.decola.tech._5.board.tarefas.persistence.dao;

import br.com.dio.decola.tech._5.board.tarefas.persistence.converter.OffsetDateTimeConverter;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;

@RequiredArgsConstructor
public class CardHistoryDAO {

    private final Connection connection;

    /**
     * Cria um registro quando o card entra em uma coluna.
     */
    public void insertEntry(Long cardId, Long columnId, OffsetDateTime enteredAt) throws SQLException {
        var sql = """
            INSERT INTO CARD_COLUMN_HISTORY (card_id, board_column_id, entered_at)
            VALUES (?, ?, ?)
            """;
        try (var ps = connection.prepareStatement(sql)) {
            ps.setLong(1, cardId);
            ps.setLong(2, columnId);
            ps.setTimestamp(3, OffsetDateTimeConverter.toTimestamp(enteredAt));
            ps.executeUpdate();
        }
    }

    /**
     * Atualiza o registro anterior para definir o exited_at quando o card sai da coluna.
     */
    public void updateExit(Long cardId, Long columnId, OffsetDateTime exitedAt) throws SQLException {
        // Aqui assumimos que o card está "na" columnId atual, e definimos o exited_at do último registro desse card
        // Se preferir usar outro critério de busca, fique à vontade
        var sql = """
            UPDATE CARD_COLUMN_HISTORY
               SET exited_at = ?
             WHERE card_id = ?
               AND board_column_id = ?
               AND exited_at IS NULL
             ORDER BY entered_at DESC
             LIMIT 1
            """;
        try (var ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, OffsetDateTimeConverter.toTimestamp(exitedAt));
            ps.setLong(2, cardId);
            ps.setLong(3, columnId);
            ps.executeUpdate();
        }
    }
}
