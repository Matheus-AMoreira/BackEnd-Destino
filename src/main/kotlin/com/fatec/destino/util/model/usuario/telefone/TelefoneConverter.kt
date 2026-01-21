package com.fatec.destino.util.model.usuario.telefone

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class TelefoneConverter : AttributeConverter<Telefone?, String?> {
    override fun convertToDatabaseColumn(attribute: Telefone?): String? {
        // Salva a string pura no DB
        return attribute?.valorPuro
    }

    override fun convertToEntityAttribute(dbData: String?): Telefone? {
        // Carrega o dado puro do DB e cria o Objeto de Valor, acionando a validação.
        return if (dbData == null) null else Telefone(dbData)
    }
}