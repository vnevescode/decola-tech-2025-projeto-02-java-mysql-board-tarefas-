package br.com.dio.decola.tech._5.board.tarefas.persistence.entity;

import lombok.Data;

@Data
public class CardEntity {

    private Long id;
    private String title;
    private String description;
    private BoardColumnEntity boardColumn = new BoardColumnEntity();
}
