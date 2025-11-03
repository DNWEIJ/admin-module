package dwe.holding.customer.client.model.converter;

import dwe.holding.customer.client.model.type.CustomerStatusEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CustomerStatusConverter implements AttributeConverter<CustomerStatusEnum, String> {

    @Override
    public String convertToDatabaseColumn(CustomerStatusEnum attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException("Null value for CustomerStatusEnum");
        }
        return attribute.getDatabaseField();
    }

    @Override
    public CustomerStatusEnum convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            throw new IllegalArgumentException("Null value in db for CustomerStatusEnum");
        }
        return CustomerStatusEnum.getEnumFromDbField(dbData);
    }
}