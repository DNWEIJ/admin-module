package dwe.holding.supplyinventory.converter;

import dwe.holding.supplyinventory.model.type.TaxTypeEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TaxTypeEnumConverter  implements AttributeConverter<TaxTypeEnum, String> {

    @Override
    public String convertToDatabaseColumn(TaxTypeEnum attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException("Null value for TaxTypeEnum");
        }
        return attribute.getDatabaseField();
    }

    @Override
    public TaxTypeEnum convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            throw new IllegalArgumentException("Null value in db for TaxTypeEnum");
        }
        return TaxTypeEnum.getEnumFromDbField(dbData);
    }
}

