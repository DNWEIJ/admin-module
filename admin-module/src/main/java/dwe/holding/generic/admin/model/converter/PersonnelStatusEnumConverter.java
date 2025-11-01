package dwe.holding.generic.admin.model.converter;

import dwe.holding.generic.admin.model.type.LanguagePrefEnum;
import dwe.holding.generic.admin.model.type.PersonnelStatusEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PersonnelStatusEnumConverter implements AttributeConverter<PersonnelStatusEnum, String> {

    @Override
    public String convertToDatabaseColumn(PersonnelStatusEnum attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException("Null value for PersonnelStatusEnum");
        }
        return attribute.getDatabaseField();
    }

    @Override
    public PersonnelStatusEnum convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            throw new IllegalArgumentException("Null value in db for PersonnelStatusEnum");
        }
        return PersonnelStatusEnum.getEnumFromDbField(dbData);
    }
}