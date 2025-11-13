package dwe.holding.salesconsult.consult.model.converter;

import dwe.holding.salesconsult.consult.model.type.VisitStatusEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class VisitStatusConverter implements AttributeConverter<VisitStatusEnum, String> {

    @Override
    public String convertToDatabaseColumn(VisitStatusEnum attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException("Null value for VisitStatusEnum");
        }
        return attribute.getDatabaseField();
    }

    @Override
    public VisitStatusEnum convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            throw new IllegalArgumentException("Null value in db for VisitStatusEnum");
        }
        return VisitStatusEnum.getEnumFromDbField(dbData);
    }
}