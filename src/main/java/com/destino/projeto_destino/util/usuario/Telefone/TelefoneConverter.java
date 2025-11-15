package com.destino.projeto_destino.util.usuario.Telefone;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TelefoneConverter implements AttributeConverter<Telefone, String> {

    @Override
    public String convertToDatabaseColumn(Telefone attribute) {
        // Salva a string pura no DB
        return attribute == null ? null : attribute.getValorPuro();
    }

    @Override
    public Telefone convertToEntityAttribute(String dbData) {
        // Carrega o dado puro do DB e cria o Objeto de Valor, acionando a validação.
        return dbData == null ? null : new Telefone(dbData);
    }
}
