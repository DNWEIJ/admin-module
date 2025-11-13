package dwe.holding.shared.model.converter;

import dwe.holding.shared.model.type.TaxedTypeEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TaxedTypeEnumConverter implements AttributeConverter<TaxedTypeEnum, String> {

    @Override
    public String convertToDatabaseColumn(TaxedTypeEnum attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException("Null value for YesNoEnum");
        }
        return attribute.getDatabaseField();
    }

    @Override
    public TaxedTypeEnum convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            throw new IllegalArgumentException("Null value in db for YesNoEnum");
        }
        return TaxedTypeEnum.getEnumFromDbField(dbData);
    }
}