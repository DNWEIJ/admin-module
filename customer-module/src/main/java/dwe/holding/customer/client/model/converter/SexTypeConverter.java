package dwe.holding.customer.client.model.converter;

import dwe.holding.customer.client.model.type.SexTypeEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SexTypeConverter implements AttributeConverter<SexTypeEnum, String> {

    @Override
    public String convertToDatabaseColumn(SexTypeEnum attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException("Null value for SexTypeEnum");
        }
        return attribute.getDatabaseField();
    }

    @Override
    public SexTypeEnum convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            throw new IllegalArgumentException("Null value in db for SexTypeEnum");
        }
        return SexTypeEnum.getEnumFromDbField(dbData);
    }
}