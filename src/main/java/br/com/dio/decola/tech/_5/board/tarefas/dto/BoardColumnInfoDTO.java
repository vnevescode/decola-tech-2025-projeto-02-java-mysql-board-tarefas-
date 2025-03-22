package br.com.dio.decola.tech._5.board.tarefas.dto;

import br.com.dio.decola.tech._5.board.tarefas.persistence.entity.BoardColumnKindEnum;

public record BoardColumnInfoDTO(Long id, int order, BoardColumnKindEnum kind) {
}
