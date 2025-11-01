package dwe.holding.generic.shared.model.converter;

import dwe.holding.generic.shared.model.type.YesNoEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class YesNoEnumConverter implements AttributeConverter<YesNoEnum, String> {

    @Override
    public String convertToDatabaseColumn(YesNoEnum attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException("Null value for YesNoEnum");
        }
        return attribute.getDatabaseField();
    }

    @Override
    public YesNoEnum convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            throw new IllegalArgumentException("Null value in db for YesNoEnum");
        }
        return YesNoEnum.getEnumFromDbField(dbData);
    }
}