package dwe.holding.customer.model.converter;

import dwe.holding.customer.model.type.CustomerStatusEnum;
import dwe.holding.customer.model.type.SexTypeEnum;
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