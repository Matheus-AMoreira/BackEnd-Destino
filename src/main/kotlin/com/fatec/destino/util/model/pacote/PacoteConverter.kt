package com.fatec.destino.util.model.pacote

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.util.*
import java.util.stream.Collectors

@Converter(autoApply = false)
class StringListConverter : AttributeConverter<MutableList<String?>?, String?> {
    override fun convertToDatabaseColumn(attribute: MutableList<String?>?): String? {
        if (attribute == null || attribute.isEmpty()) {
            return null
        }
        return attribute.stream()
            .map<String?> { obj: String? -> obj!!.trim { it <= ' ' } }
            .collect(Collectors.joining(SEPARATOR))
    }

    override fun convertToEntityAttribute(dbData: String?): MutableList<String?> {
        if (dbData == null || dbData.trim { it <= ' ' }.isEmpty()) {
            return mutableListOf<String?>()
        }
        return Arrays.stream<String>(dbData.split(SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
            .map<String?> { obj: String? -> obj!!.trim { it <= ' ' } }
            .collect(Collectors.toList())
    }

    companion object {
        private const val SEPARATOR = ";"
    }
}
