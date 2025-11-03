/*
Administrador
email:administrador@destino.com
senha: dJGu83561SUWP!
Funcionário
email:funcionario@destino.com
senha: FuNc!2834984
Usuário
email:usuario@destino.com
senha: Us-Rio3245!
*/
insert into `destino`.`usu_usuario`
(`usu_id`,`usu_data_atualizacao`,`usu_data_cadastro`,`usu_cpf`,`usu_email`,`usu_nome`,`USU_PERFIL`,
`usu_senha`,`usu_telefone`,`usu_valido`) values
(UUID_TO_BIN(UUID()),NOW(), NOW(),'16506961082','administrador@destino.com','Aministrador','ADMINISTRADOR','dJGu83561SUWP!','4438858699',1),
(UUID_TO_BIN(UUID()),NOW(), NOW(),'71827029080','funcionario@destino.com','Funcionário','FUNCIONARIO','FuNc!2834984','44981811400',1),
(UUID_TO_BIN(UUID()),NOW(), NOW(),'25625316554','usuario@destino.com','Usuário','USUARIO','Us-Rio3245!','44981811400',1);