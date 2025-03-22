package br.com.dio.decola.tech._5.board.tarefas.dto;

import br.com.dio.decola.tech._5.board.tarefas.persistence.entity.BoardColumnKindEnum;

public record BoardColumnDTO(Long id,
                             String name,
                             BoardColumnKindEnum kind,
                             int cardsAmount) {
}