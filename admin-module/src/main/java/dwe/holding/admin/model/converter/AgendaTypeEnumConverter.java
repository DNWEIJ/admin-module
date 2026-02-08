package dwe.holding.admin.model.converter;

import dwe.holding.admin.model.type.AgendaTypeEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AgendaTypeEnumConverter implements AttributeConverter<AgendaTypeEnum, String> {

    @Override
    public String convertToDatabaseColumn(AgendaTypeEnum attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException("Null value for AgendaTypeEnum");
        }
        return attribute.getDatabaseField();
    }

    @Override
    public AgendaTypeEnum convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            throw new IllegalArgumentException("Null value in db for AgendaTypeEnum");
        }
        return AgendaTypeEnum.getEnumFromDbField(dbData);
    }
}