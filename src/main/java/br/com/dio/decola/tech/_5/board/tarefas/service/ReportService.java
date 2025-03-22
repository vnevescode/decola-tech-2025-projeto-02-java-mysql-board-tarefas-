package br.com.dio.decola.tech._5.board.tarefas.service;

import br.com.dio.decola.tech._5.board.tarefas.dto.BlockReportDTO;
import br.com.dio.decola.tech._5.board.tarefas.dto.ColumnTimeReportDTO;
import br.com.dio.decola.tech._5.board.tarefas.persistence.converter.OffsetDateTimeConverter;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ReportService {

    private final Connection connection;

    /**
     * Retorna uma lista com todas as movimentações de todos os cards de um board,
     * mostrando quanto tempo cada card ficou em cada coluna.
     */
    public List<ColumnTimeReportDTO> getTimeReportForBoard(Long boardId) throws SQLException {
        var sql = """
           SELECT c.id as card_id,
                  c.title as card_title,
                  bc.name as column_name,
                  hist.entered_at,
                  hist.exited_at
             FROM CARD_COLUMN_HISTORY hist
             JOIN CARDS c ON c.id = hist.card_id
             JOIN BOARDS_COLUMNS bc ON bc.id = hist.board_column_id
             JOIN BOARDS b ON b.id = bc.board_id
            WHERE b.id = ?
            ORDER BY c.id, hist.entered_at
        """;

        try (var ps = connection.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            ps.executeQuery();
            var rs = ps.getResultSet();

            var result = new ArrayList<ColumnTimeReportDTO>();
            while (rs.next()) {
                var cardId = rs.getLong("card_id");
                var cardTitle = rs.getString("card_title");
                var columnName = rs.getString("column_name");
                var enteredAt = OffsetDateTimeConverter.toOffsetDateTime(rs.getTimestamp("entered_at"));
                var exitedAt = OffsetDateTimeConverter.toOffsetDateTime(rs.getTimestamp("exited_at"));

                long durationInSeconds = 0L;
                if (exitedAt != null) {
                    durationInSeconds = exitedAt.toEpochSecond() - enteredAt.toEpochSecond();
                }

                var dto = new ColumnTimeReportDTO(
                        cardId,
                        cardTitle,
                        columnName,
                        enteredAt,
                        exitedAt,
                        durationInSeconds
                );
                result.add(dto);
            }
            return result;
        }
    }

    public List<BlockReportDTO> getBlockReportForBoard(Long boardId) throws SQLException {
        var sql = """
       SELECT c.id AS card_id,
              c.title AS card_title,
              b.blocked_at,
              b.unblocked_at,
              b.block_reason,
              b.unblock_reason
         FROM BLOCKS b
         JOIN CARDS c ON c.id = b.card_id
         JOIN BOARDS_COLUMNS bc ON bc.id = c.board_column_id
         JOIN BOARDS bo ON bo.id = bc.board_id
        WHERE bo.id = ?
        ORDER BY c.id, b.blocked_at
    """;

        try (var ps = connection.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            ps.executeQuery();
            var rs = ps.getResultSet();
            var list = new ArrayList<BlockReportDTO>();
            while (rs.next()) {
                var blockedAt = OffsetDateTimeConverter.toOffsetDateTime(rs.getTimestamp("blocked_at"));
                var unblockedAt = OffsetDateTimeConverter.toOffsetDateTime(rs.getTimestamp("unblocked_at"));
                long durationInSeconds = 0L;
                if (unblockedAt != null) {
                    durationInSeconds = unblockedAt.toEpochSecond() - blockedAt.toEpochSecond();
                }
                var dto = new BlockReportDTO(
                        rs.getLong("card_id"),
                        rs.getString("card_title"),
                        blockedAt,
                        unblockedAt,
                        rs.getString("block_reason"),
                        rs.getString("unblock_reason"),
                        durationInSeconds
                );
                list.add(dto);
            }
            return list;
        }
    }

}
