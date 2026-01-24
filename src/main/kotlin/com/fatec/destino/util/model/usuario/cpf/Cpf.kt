package com.fatec.destino.util.model.usuario.cpf

import java.io.Serializable

class Cpf(dbData: String) : Serializable {

    private var valor: String? = null

    init {
        require(isCpfvalido(dbData)) { "CPF fornecido é inválido." }
        this.valor = dbData
    }

    fun Cpf(cpf: String) {
        require(isCpfvalido(cpf)) { "CPF fornecido é inválido." }

        valor = cpf
    }

    fun getvalorPuro(): String? {
        return valor
    }

    fun getvalorFormatado(): String? {
        if (valor == null || valor!!.length != 11) {
            return valor
        }
        return valor!!.substring(0, 3) + "." +
                valor!!.substring(3, 6) + "." +
                valor!!.substring(6, 9) + "-" +
                valor!!.substring(9, 11)
    }

    private fun isCpfvalido(cpf: String): Boolean {
        if (cpf.length != 11) {
            return false
        }

        // verifica sequências repetidas
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

        val digitoverificador = 11 - (soma % 11)

        return if (digitoverificador >= 10) 0 else digitoverificador
    }
}
