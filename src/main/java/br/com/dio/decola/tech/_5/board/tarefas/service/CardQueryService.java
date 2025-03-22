package br.com.dio.decola.tech._5.board.tarefas.service;

import br.com.dio.decola.tech._5.board.tarefas.dto.CardDetailsDTO;
import br.com.dio.decola.tech._5.board.tarefas.dto.ColumnTimeReportDTO;
import br.com.dio.decola.tech._5.board.tarefas.persistence.converter.OffsetDateTimeConverter;
import br.com.dio.decola.tech._5.board.tarefas.persistence.dao.CardDAO;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class CardQueryService {

    private final Connection connection;

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        var dao = new CardDAO(connection);
        return dao.findById(id);
    }

    public List<ColumnTimeReportDTO> getTimeReportForCard(Long cardId) throws SQLException {
        // Precisamos unir: CARD_COLUMN_HISTORY + BOARDS_COLUMNS + CARDS
        var sql = """
       SELECT c.id as card_id,
              c.title as card_title,
              bc.name as column_name,
              hist.entered_at,
              hist.exited_at
         FROM CARD_COLUMN_HISTORY hist
         JOIN CARDS c ON c.id = hist.card_id
         JOIN BOARDS_COLUMNS bc ON bc.id = hist.board_column_id
        WHERE c.id = ?
        ORDER BY hist.entered_at
    """;

        try (var ps = connection.prepareStatement(sql)) {
            ps.setLong(1, cardId);
            ps.executeQuery();
            var rs = ps.getResultSet();
            var list = new ArrayList<ColumnTimeReportDTO>();
            while (rs.next()) {
                var entered = OffsetDateTimeConverter.toOffsetDateTime(rs.getTimestamp("entered_at"));
                var exited = OffsetDateTimeConverter.toOffsetDateTime(rs.getTimestamp("exited_at"));
                var duration = 0L;
                if (exited != null) {
                    duration = exited.toEpochSecond() - entered.toEpochSecond();
                }
                list.add(new ColumnTimeReportDTO(
                        rs.getLong("card_id"),
                        rs.getString("card_title"),
                        rs.getString("column_name"),
                        entered,
                        exited,
                        duration
                ));
            }
            return list;
        }
    }
}
