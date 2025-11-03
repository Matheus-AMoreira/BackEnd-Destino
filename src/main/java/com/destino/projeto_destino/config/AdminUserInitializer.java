package com.destino.projeto_destino.config;

import com.destino.projeto_destino.model.Usuario;
import com.destino.projeto_destino.model.UsuarioUtils.Email.Email;
import com.destino.projeto_destino.model.UsuarioUtils.UserRole;
import com.destino.projeto_destino.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        final String adminEmail = "admin@destino.com";
        final String adminSenha = "Admin@#123";
        final String adminCpf = "50992858054";

        try {
            Email emailObj = new Email(adminEmail);

            if (userRepository.findByEmail(emailObj).isPresent()) {
                logger.info("Usuário admin já existe. Nenhuma ação necessária.");
                return;
            }

            logger.info("Usuário admin não encontrado, criando novo usuário...");

            Usuario adminUser = new Usuario();
            adminUser.setNome("Administrador");
            adminUser.setEmail(adminEmail);
            adminUser.setCpf(adminCpf);
            adminUser.setTelefone("11999999999");
            adminUser.setSenha(passwordEncoder.encode(adminSenha));

            adminUser.setPerfil(UserRole.ADMINISTRADOR);
            adminUser.setValido(true);

            userRepository.save(adminUser);

            logger.info("Usuário admin criado com sucesso!");

        } catch (IllegalArgumentException e) {
            logger.error("Falha ao criar usuário admin devido a dados inválidos: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Erro inesperado durante a inicialização do usuário admin", e);
        }
    }
}
