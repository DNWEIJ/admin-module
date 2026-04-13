package dwe.holding.supplyinventory.model.converter;

import dwe.holding.supplyinventory.model.type.PricingTypeEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PricingTypeEnumConverter implements AttributeConverter<PricingTypeEnum, String> {

    @Override
    public String convertToDatabaseColumn(PricingTypeEnum attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException("Null value for PricingTypeEnum");
        }
        return attribute.getDatabaseField();
    }

    @Override
    public PricingTypeEnum convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            throw new IllegalArgumentException("Null value in db for PricingTypeEnum");
        }
        return PricingTypeEnum.getEnumFromDbField(dbData);
    }
}