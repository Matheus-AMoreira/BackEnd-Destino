package com.fatec.destino.util.model.usuario.cpf

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class CpfConverter : AttributeConverter<Cpf?, String?> {
    override fun convertToDatabaseColumn(attribute: Cpf?): String? {
        return if (attribute == null) null else attribute.getValorPuro()
    }

    override fun convertToEntityAttribute(dbData: String?): Cpf? {
        return if (dbData == null) null else Cpf(dbData)
    }
}