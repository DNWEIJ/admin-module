package dwe.holding.generic.admin.model.converter;

import dwe.holding.generic.admin.model.type.LanguagePrefEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LanguagePrefEnumConverter implements AttributeConverter<LanguagePrefEnum, String> {

    @Override
    public String convertToDatabaseColumn(LanguagePrefEnum attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException("Null value for LanguagePrefEnum");
        }
        return attribute.getDatabaseField();
    }

    @Override
    public LanguagePrefEnum convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            throw new IllegalArgumentException("Null value in db for LanguagePrefEnum");
        }
        return LanguagePrefEnum.getEnumFromDbField(dbData);
    }
}