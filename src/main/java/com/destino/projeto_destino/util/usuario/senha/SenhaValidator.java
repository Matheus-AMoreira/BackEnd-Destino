package com.destino.projeto_destino.util.usuario.senha;

import java.util.regex.Pattern;

public class SenhaValidator {

    // Regex completo que verifica todos os requisitos simultaneamente
    // ^                   # Início da string
    // (?=.*[0-9])         # Deve conter pelo menos um dígito
    // (?=.*[a-z])         # Deve conter pelo menos uma letra minúscula
    // (?=.*[A-Z])         # Deve conter pelo menos uma letra maiúscula
    // (?=.*[!@#$%^&*()_+-]) # Deve conter pelo menos um caractere especial (lista customizável)
    // .{8,}               # Deve ter pelo menos 8 caracteres (Ajuste conforme necessário)
    // $                   # Fim da string
    private static final String SENHA_COMPLEXA_REGEX =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-]).{8,}$";

    private static final Pattern PATTERN = Pattern.compile(SENHA_COMPLEXA_REGEX);

    public static boolean isValid(String senha) {
        if (senha == null) {
            return false;
        }
        return PATTERN.matcher(senha).matches();
    }
}
