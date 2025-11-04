package com.destino.projeto_destino.model.usuarioUtils.Cpf;

import java.io.Serializable;

public class Cpf implements Serializable {

    private final String Valor;

    public Cpf(String cpf) {

        if (!isCpfValido(cpf)) {
            throw new IllegalArgumentException("CPF fornecido é inválido.");
        }

        this.Valor = cpf;
    }

    public String getValorPuro() {
        return this.Valor;
    }

    public String getValorFormatado() {
        if (Valor == null || Valor.length() != 11) {
            return Valor;
        }
        return Valor.substring(0, 3) + "." +
                Valor.substring(3, 6) + "." +
                Valor.substring(6, 9) + "-" +
                Valor.substring(9, 11);
    }

    private boolean isCpfValido(String cpf) {
        if (cpf.length() != 11) {
            return false;
        }

        // Verifica sequências repetidas
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
        if (digito2 != Integer.parseInt(cpf.substring(10, 11))) {
            return false;
        }

        return true;
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

        int digitoVerificador = 11 - (soma % 11);

        return (digitoVerificador >= 10) ? 0 : digitoVerificador;
    }
}
