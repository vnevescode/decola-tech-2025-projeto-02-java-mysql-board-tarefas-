--liquibase formatted sql
--changeset victor:202503212046
--comment: create column history

CREATE TABLE IF NOT EXISTS CARD_COLUMN_HISTORY (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    card_id BIGINT NOT NULL,
    board_column_id BIGINT NOT NULL,
    entered_at TIMESTAMP NOT NULL,
    exited_at TIMESTAMP NULL,
    CONSTRAINT fk_card_history_card FOREIGN KEY (card_id) REFERENCES CARDS(id),
    CONSTRAINT fk_card_history_column FOREIGN KEY (board_column_id) REFERENCES BOARDS_COLUMNS(id)
);