package br.com.dio.decola.tech._5.board.tarefas.exception;

public class CardFinishedException extends RuntimeException{

    public CardFinishedException(final String message) {
        super(message);
    }
}