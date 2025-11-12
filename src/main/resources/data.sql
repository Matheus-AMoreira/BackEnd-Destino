-- -----------------------------------------------------
-- 1. Inserção de Usuários (`usu_usuario`)
-- -----------------------------------------------------

-- Gerando UUIDs para Usuários (serão usados como chaves estrangeiras)
SET @USU_FUN_ID = UNHEX(REPLACE(UUID(), '-', ''));
SET @USU_COMUM_ID = UNHEX(REPLACE(UUID(), '-', ''));

INSERT IGNORE INTO `usu_usuario` (`usu_id`, `usu_data_atualizacao`, `usu_data_cadastro`, `usu_cpf`, `usu_email`, `usu_nome`, `usu_perfil`, `usu_senha`, `usu_sobrenome`, `usu_telefone`, `usu_valido`) VALUES
(@USU_FUN_ID, NULL, '2025-11-02 11:30:00.000000', '55566677788', 'func@destino.com', 'Ana', 'FUNCIONARIO', '$2a$10$OUTRO_HASH', 'Vendedora', '11998877665', 1),
(@USU_COMUM_ID, NULL, '2025-11-03 14:45:00.000000', '00011122233', 'cliente@email.com', 'João', 'USUARIO', '$2a$10$TERCEIRO_HASH', 'Silva', '11977775555', 1);

-- -----------------------------------------------------
-- 2. Inserção de Hotéis (`hot_hotel`)
-- -----------------------------------------------------

INSERT IGNORE INTO `hot_hotel` (`hot_id`, `hot_cidade`, `hot_endereco`, `hot_nome`) VALUES
(1, 'Rio de Janeiro', 'Av. Atlântica, 1000', 'Hotel Praiano Sol'),
(2, 'Gramado', 'Rua das Hortênsias, 500', 'Serra Encantada');


-- -----------------------------------------------------
-- 3. Inserção de Pacotes (`pac_pacote`)
-- -----------------------------------------------------

INSERT IGNORE INTO `pac_pacote` (`pac_id`, `pac_data_inicio_viagem`, `pac_descricao`, `pac_disponibilidade`, `pac_local`, `pac_nome`, `pac_preco`, `pac_status`, `pac_transporte`, `usu_id`, `hot_id`) VALUES
(101, '2026-03-15 08:00:00.000000', '7 dias de sol e praia no RJ, com café da manhã e passeios.', 50, 'Rio de Janeiro/RJ', 'Férias Cariocas', 1850.00, 'DISPONIVEL', 'AEREA', @USU_FUN_ID, 1),
(102, '2026-06-20 12:00:00.000000', 'Pacote de inverno para a Serra Gaúcha, com traslado rodoviário e 4 noites de hospedagem.', 30, 'Gramado/RS', 'Inverno na Serra', 1200.00, 'DISPONIVEL', 'RODOVIARIA', @USU_FUN_ID, 2);


-- -----------------------------------------------------
-- 4. Inserção de Fotos (`fot_foto`)
-- -----------------------------------------------------

INSERT IGNORE INTO `fot_foto` (`fot_id`, `fot_nome`, `fot_url`) VALUES
(201, 'Praia RJ', 'https://storage.destino.com/pacote101/foto1.jpg'),
(202, 'Neve Gramado', 'https://storage.destino.com/pacote102/foto1.jpg');


-- -----------------------------------------------------
-- 5. Inserção da Relação Pacote-Foto (`pcf_pacote_foto`)
-- -----------------------------------------------------

INSERT IGNORE INTO `pcf_pacote_foto` (`pcf_id`, `fot_id`, `pac_id`) VALUES
(1, 201, 101),
(2, 202, 102);


-- -----------------------------------------------------
-- 6. Inserção de Pagamentos (`pag_pagamento`)
-- -----------------------------------------------------

INSERT IGNORE INTO `pag_pagamento` (`pag_id`, `pag_desconto`, `pag_meio`, `pag_metodo`, `pag_processamento`, `pag_valor_bruto`, `pag_valor_final`) VALUES
(301, 50.00, 'VISA', 'PARCELADO', 'ACEITO', 1850.00, 1800.00), -- Pagamento para Pacote 101
(302, 0.00, 'UOL', 'VISTA', 'PENDENTE', 1200.00, 1200.00); -- Pagamento para Pacote 102


-- -----------------------------------------------------
-- 7. Inserção de Compras (`com_compra`)
-- -----------------------------------------------------

SET @COM_ID_1 = UNHEX(REPLACE(UUID(), '-', ''));
SET @COM_ID_2 = UNHEX(REPLACE(UUID(), '-', ''));

INSERT IGNORE INTO `com_compra` (`com_id`, `com_data_compra`, `com_status`, `pac_id`, `pag_id`, `usu_id`) VALUES
(@COM_ID_1, '2025-11-10 09:15:30.000000', 'COMPRADO', 101, 301, @USU_COMUM_ID),
(@COM_ID_2, '2025-11-10 11:20:00.000000', 'PENDENTE', 102, 302, @USU_COMUM_ID);


-- -----------------------------------------------------
-- 8. Inserção de Avaliações (`ava_avaliacao`)
-- -----------------------------------------------------

INSERT IGNORE INTO `ava_avaliacao` (`ava_id`, `ava_comentario`, `ava_data`, `ava_nota`, `pac_id`, `usu_id`) VALUES
(401, 'Hotel e pacote excelentes, experiência incrível!', '2025-11-11 10:00:00.000000', 5, 101, @USU_COMUM_ID);