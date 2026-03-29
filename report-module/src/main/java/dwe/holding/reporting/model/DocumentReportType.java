package dwe.holding.reporting.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import dwe.holding.shared.model.frontend.PresentationElement;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Getter
public enum DocumentReportType {


    GENERIC("G", "label.documenttype.generic"),
    SEARCH_CUSTOMER("S", "label.documenttype.searchcustomer"),
    REMINDER("R", "label.documenttype.reminder"),
    EMAIL_MASTER("E","label.documenttype.emailmaster");
    @JsonValue
    private final String databaseField;
    private final String label;


    DocumentReportType(String databaseField, String label) {
        this.databaseField = databaseField;
        this.label = label;
    }

    public static DocumentReportType getEnum(String value) {
        if (value == null)
            throw new IllegalArgumentException("Null value for DocumentReportType");
        for (DocumentReportType anEnum : values())
            if (value.equalsIgnoreCase(anEnum.name())) return anEnum;
        throw new IllegalArgumentException();
    }

    @JsonCreator
    public static DocumentReportType getEnumFromDbField(String value) {
        if (value == null)
            throw new IllegalArgumentException("Null value in db for DocumentReportType");
        for (DocumentReportType anEnum : values())
            if (value.equalsIgnoreCase(anEnum.getDatabaseField())) return anEnum;
        throw new IllegalArgumentException();
    }

    public static List<DocumentReportType> getWebList() {
        return Arrays.stream(DocumentReportType.values()).toList();
    }

    public static List<PresentationElement> getWebListDoNotCare() {

        return Stream.concat(
                getWebList().stream().map(a -> new PresentationElement(a.name(), a.name(), a.getLabel())),
                Stream.of(new PresentationElement("doNotCare", "doNotCare", "label.yesno.donotcare"))
        ).toList();
    }
}
