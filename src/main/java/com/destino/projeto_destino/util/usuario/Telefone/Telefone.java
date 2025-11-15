package com.destino.projeto_destino.util.usuario.Telefone;

import jakarta.validation.constraints.Size;

import java.io.Serializable;

public class Telefone implements Serializable {

    @Size(min=10, max=11, message="Número de telefone é fixo ou movel com 10 ou 11 digitos")
    private final String numeroPuro;

    public Telefone(String telefonePuro) {
        if (!isTelefoneValido(telefonePuro)) {
            throw new IllegalArgumentException("Número de telefone inválido. Deve conter 10 ou 11 dígitos.");
        }

        this.numeroPuro = telefonePuro;
    }
    private boolean isTelefoneValido(String telefone) {
        if (telefone == null) return false;

        if (!telefone.matches("^\\d{10,11}$")) {
            return false;
        }

        return true;
    }
    public String getValorPuro() {
        return this.numeroPuro;
    }

    public String getValorFormatado() {
        if (numeroPuro == null) return null;

        String ddd = numeroPuro.substring(0, 2);
        String numero = numeroPuro.substring(2);

        if (numero.length() == 9) {
            // Celular: (DD) 9XXXX-XXXX
            return "(" + ddd + ") " + numero.substring(0, 5) + "-" + numero.substring(5);
        } else if (numero.length() == 8) {
            // Fixo: (DD) XXXX-XXXX
            return "(" + ddd + ") " + numero.substring(0, 4) + "-" + numero.substring(4);
        }

        return numeroPuro;
    }
}
