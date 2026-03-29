package dwe.holding.reporting.converter;

import dwe.holding.reporting.model.DocumentReportType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DocumentReportTypeConverter implements AttributeConverter<DocumentReportType, String> {

    @Override
    public String convertToDatabaseColumn(DocumentReportType attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException("Null value for DocumentReportType");
        }
        return attribute.getDatabaseField();
    }

    @Override
    public DocumentReportType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            throw new IllegalArgumentException("Null value in db for DocumentReportType");
        }
        return DocumentReportType.getEnumFromDbField(dbData);
    }
}