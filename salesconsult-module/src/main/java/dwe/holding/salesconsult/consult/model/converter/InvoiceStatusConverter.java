package dwe.holding.salesconsult.consult.model.converter;

import dwe.holding.salesconsult.consult.model.type.InvoiceStatusEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class InvoiceStatusConverter implements AttributeConverter<InvoiceStatusEnum, String> {

    @Override
    public String convertToDatabaseColumn(InvoiceStatusEnum attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException("Null value for InvoiceStatusEnum");
        }
        return attribute.getDatabaseField();
    }

    @Override
    public InvoiceStatusEnum convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            throw new IllegalArgumentException("Null value in db for InvoiceStatusEnum");
        }
        return InvoiceStatusEnum.getEnumFromDbField(dbData);
    }
}