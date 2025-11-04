package com.destino.projeto_destino.config;

import com.destino.projeto_destino.model.Usuario;
import com.destino.projeto_destino.model.usuarioUtils.Cpf.Cpf;
import com.destino.projeto_destino.model.usuarioUtils.Email.Email;
import com.destino.projeto_destino.model.usuarioUtils.Telefone.Telefone;
import com.destino.projeto_destino.model.usuarioUtils.UserRole;
import com.destino.projeto_destino.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Iniciando verificação de dados iniciais...");

        // 1. Administrador
        criarUsuarioSeNaoExistir(
                "Aministrador",
                "16506961082",
                "administrador@destino.com",
                "4438858699",
                "dJGu83561SUWP!",
                UserRole.ADMINISTRADOR,
                true
        );

        // 2. Funcionário
        criarUsuarioSeNaoExistir(
                "Funcionário",
                "71827029080",
                "funcionario@destino.com",
                "44981811400",
                "FuNc!2834984",
                UserRole.FUNCIONARIO,
                true
        );

        // 3. Usuário
        criarUsuarioSeNaoExistir(
                "Usuário",
                "25625316554",
                "usuario@destino.com",
                "44981811400",
                "Us-Rio3245!",
                UserRole.USUARIO,
                true
        );

        logger.info("Verificação de dados iniciais concluída.");
    }

    private void criarUsuarioSeNaoExistir(String nome, String cpf, String email,
                                          String telefone, String senhaPlana,
                                          UserRole perfil, boolean valido) {
        try {
            Email emailObj = new Email(email);

            if (usuarioRepository.findByEmail(emailObj).isEmpty()) {

                Cpf cpfObj = new Cpf(cpf);
                Telefone telObj = new Telefone(telefone);

                String senhaHasheada = passwordEncoder.encode(senhaPlana);

                Usuario novoUsuario = new Usuario(
                        nome,
                        cpfObj,
                        emailObj,
                        telObj,
                        senhaHasheada,
                        perfil,
                        valido
                );

                usuarioRepository.save(novoUsuario);
                logger.info("Usuário criado: {}", email);
            } else {
                logger.warn("Usuário já existe, não foi criado: {}", email);
            }
        } catch (Exception e) {
            logger.error("Falha ao criar usuário {}: {}", email, e.getMessage());
        }
    }
}
