--liquibase formatted sql
--changeset victor:202503212229
--comment: Permitir NULL em unblock_reason


ALTER TABLE BLOCKS
    MODIFY COLUMN unblock_reason VARCHAR(255) NULL;

--rollback ALTER TABLE BLOCKS
--         MODIFY COLUMN unblock_reason VARCHAR(255) NOT NULL;