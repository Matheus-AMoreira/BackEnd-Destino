package com.fatec.destino.util.repository

import com.fatec.destino.util.model.pacote.PacoteStatus
import com.fatec.destino.util.model.transporte.Meio

interface StatusCount {
    val status: PacoteStatus
    val total: Long
}

interface MeioCount {
    val meio: Meio
    val total: Long
}

interface MonthCount {
    val mes: Int
    val total: Long
}