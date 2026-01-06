package com.fatec.destino.util.model.usuario.Cpf;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CpfConverter implements AttributeConverter<Cpf, String> {

    @Override
    public String convertToDatabaseColumn(Cpf attribute) {
        return attribute == null ? null : attribute.getValorPuro();
    }

    @Override
    public Cpf convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new Cpf(dbData);
    }
}
