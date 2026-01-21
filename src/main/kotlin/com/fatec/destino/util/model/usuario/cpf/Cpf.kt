package com.fatec.destino.util.model.usuario.cpf

import java.io.Serializable

class Cpf(dbData: String) : Serializable {

    private var Valor: String? = null

    fun Cpf(cpf: String) {
        require(isCpfValido(cpf)) { "CPF fornecido é inválido." }

        Valor = cpf
    }

    fun getValorPuro(): String? {
        return Valor
    }

    fun getValorFormatado(): String? {
        if (Valor == null || Valor!!.length != 11) {
            return Valor
        }
        return Valor!!.substring(0, 3) + "." +
                Valor!!.substring(3, 6) + "." +
                Valor!!.substring(6, 9) + "-" +
                Valor!!.substring(9, 11)
    }

    private fun isCpfValido(cpf: String): Boolean {
        if (cpf.length != 11) {
            return false
        }

        // Verifica sequências repetidas
        if (isSequenciaRepetida(cpf)) {
            return false
        }

        // Calcula o 1º dígito verificador
        val digito1 = calcularDigito(cpf.substring(0, 9), 10)
        if (digito1 != cpf.substring(9, 10).toInt()) {
            return false
        }

        // Calcula o 2º dígito verificador
        val digito2 = calcularDigito(cpf.substring(0, 10), 11)
        if (digito2 != cpf.substring(10, 11).toInt()) {
            return false
        }

        return true
    }

    private fun isSequenciaRepetida(cpf: String): Boolean {
        val primeiro = cpf.get(0)
        for (i in 1..<cpf.length) {
            if (cpf.get(i) != primeiro) {
                return false
            }
        }
        return true
    }

    private fun calcularDigito(numeros: String, pesoInicial: Int): Int {
        var soma = 0
        var peso = pesoInicial
        for (i in 0..<numeros.length) {
            soma += numeros.substring(i, i + 1).toInt() * peso
            peso--
        }

        val digitoVerificador = 11 - (soma % 11)

        return if (digitoVerificador >= 10) 0 else digitoVerificador
    }
}
