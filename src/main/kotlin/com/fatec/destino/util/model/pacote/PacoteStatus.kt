package com.fatec.destino.util.model.pacote

enum class PacoteStatus(val exibicao: String) {
    CONCLUIDO("Concluídas"),
    EMANDAMENTO("Em Andamento"),
    CANCELADO("Canceladas")
}

enum class Meio(val exibicao: String) {
    AEREO("Aéreo"),
    MARITIMO("Marítimo"),
    TERRESTRE("Terrestre")
}
