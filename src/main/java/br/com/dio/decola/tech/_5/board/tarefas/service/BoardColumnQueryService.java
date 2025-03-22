package br.com.dio.decola.tech._5.board.tarefas.service;

import br.com.dio.decola.tech._5.board.tarefas.persistence.dao.BoardColumnDAO;
import br.com.dio.decola.tech._5.board.tarefas.persistence.entity.BoardColumnEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BoardColumnQueryService {
    private final Connection connection;

    public Optional<BoardColumnEntity> findById(final Long id) throws SQLException {
        var dao = new BoardColumnDAO(connection);
        return dao.findById(id);
    }
}
