package com.fatec.destino.util.model.usuario.Cpf;

import java.io.Serializable;

public record Cpf(String valor) implements Serializable {

    public Cpf {
        if (valor == null || !isCpfvalido(valor)) {
            throw new IllegalArgumentException("CPF fornecido é inválido.");
        }
    }

    public String getValorPuro() {
        return this.valor;
    }

    public String getValorFormatado() {
        if (valor == null || valor.length() != 11) {
            return valor;
        }
        return valor.substring(0, 3) + "." +
                valor.substring(3, 6) + "." +
                valor.substring(6, 9) + "-" +
                valor.substring(9, 11);
    }

    private boolean isCpfvalido(String cpf) {
        if (cpf.length() != 11) {
            return false;
        }

        // verifica sequências repetidas
        if (isSequenciaRepetida(cpf)) {
            return false;
        }

        // Calcula o 1º dígito verificador
        int digito1 = calcularDigito(cpf.substring(0, 9), 10);
        if (digito1 != Integer.parseInt(cpf.substring(9, 10))) {
            return false;
        }

        // Calcula o 2º dígito verificador
        int digito2 = calcularDigito(cpf.substring(0, 10), 11);
        return digito2 == Integer.parseInt(cpf.substring(10, 11));
    }

    private static boolean isSequenciaRepetida(String cpf) {
        char primeiro = cpf.charAt(0);
        for (int i = 1; i < cpf.length(); i++) {
            if (cpf.charAt(i) != primeiro) {
                return false;
            }
        }
        return true;
    }

    private static int calcularDigito(String numeros, int pesoInicial) {
        int soma = 0;
        int peso = pesoInicial;
        for (int i = 0; i < numeros.length(); i++) {
            soma += Integer.parseInt(numeros.substring(i, i + 1)) * peso;
            peso--;
        }

        int digitoverificador = 11 - (soma % 11);

        return (digitoverificador >= 10) ? 0 : digitoverificador;
    }
}
