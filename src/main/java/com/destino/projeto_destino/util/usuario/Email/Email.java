package com.destino.projeto_destino.util.usuario.Email;

import java.io.Serializable;
import java.util.regex.Pattern;

public class Email implements Serializable {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE
    );

    private final String endereco;

    public Email(String endereco) {
        if (endereco == null || endereco.trim().isEmpty()) {
            throw new IllegalArgumentException("E-mail não pode ser vazio.");
        }
        String emailNormalizado = endereco.trim().toLowerCase();

        if (!isValid(emailNormalizado)) {
            throw new IllegalArgumentException("Formato de e-mail inválido.");
        }

        this.endereco = emailNormalizado;
    }


    private boolean isValid(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public String getValor() {
        return this.endereco;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return endereco.equals(email.endereco);
    }

    @Override
    public int hashCode() {
        return endereco.hashCode();
    }
}
