package com.fatec.destino.util.model.usuario.telefone

import jakarta.validation.constraints.Size
import java.io.Serializable

class Telefone(telefonePuro: String?) : Serializable {
    val valorPuro: @Size(
        min = 10,
        max = 11,
        message = "Número de telefone é fixo ou movel com 10 ou 11 digitos"
    ) String?

    init {
        require(isTelefoneValido(telefonePuro)) { "Número de telefone inválido. Deve conter 10 ou 11 dígitos." }

        this.valorPuro = telefonePuro
    }

    private fun isTelefoneValido(telefone: String?): Boolean {
        if (telefone == null) return false

        if (!telefone.matches("^\\d{10,11}$".toRegex())) {
            return false
        }

        return true
    }

    val valorFormatado: String?
        get() {
            if (this.valorPuro == null) return null

            val ddd = valorPuro.substring(0, 2)
            val numero = valorPuro.substring(2)

            if (numero.length == 9) {
                // Celular: (DD) 9XXXX-XXXX
                return "(" + ddd + ") " + numero.substring(0, 5) + "-" + numero.substring(5)
            } else if (numero.length == 8) {
                // Fixo: (DD) XXXX-XXXX
                return "(" + ddd + ") " + numero.substring(0, 4) + "-" + numero.substring(4)
            }

            return this.valorPuro
        }
}