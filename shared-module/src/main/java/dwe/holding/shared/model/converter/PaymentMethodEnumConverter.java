package dwe.holding.shared.model.converter;

import dwe.holding.shared.model.type.PaymentMethodEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentMethodEnumConverter implements AttributeConverter<PaymentMethodEnum, String> {

    @Override
    public String convertToDatabaseColumn(PaymentMethodEnum attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException("Null value for PaymentMethodEnum");
        }
        return attribute.getDatabaseField();
    }

    @Override
    public PaymentMethodEnum convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            throw new IllegalArgumentException("Null value in db for PaymentMethodEnum");
        }
        return PaymentMethodEnum.getEnumFromDbField(dbData);
    }
}