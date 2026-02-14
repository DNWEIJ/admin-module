package dwe.holding.shared.model.type;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import dwe.holding.shared.model.frontend.PresentationElement;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Object representation of a Yes No object.
 * Can be overridden with the database information (language-specific) for this domain.
 * <p>
 * This enum is also used in preferences settings that are stored as json (userPreferences localMemberPreferences etc) hence the json annotations
 */
@Getter
public enum YesNoEnum {

    Yes("Y", "label.yesno.yes", 1),
    No("N", "label.yesno.no", 2);

    @JsonValue
    private final String databaseField;
    private final String label;
    private final int order;

    YesNoEnum(String databaseField, String label, int order) {
        this.databaseField = databaseField;
        this.order = order;
        this.label = label;
    }

    public static YesNoEnum getEnum(String value) {
        if (value == null)
            throw new IllegalArgumentException("Null value for YesNoEnum");
        for (YesNoEnum anEnum : values())
            if (value.equalsIgnoreCase(anEnum.name())) return anEnum;
        throw new IllegalArgumentException();
    }

    @JsonCreator
    public static YesNoEnum getEnumFromDbField(String value) {
        if (value == null)
            throw new IllegalArgumentException("Null value in db for YesNoEnum");
        for (YesNoEnum anEnum : values())
            if (value.equalsIgnoreCase(anEnum.getDatabaseField())) return anEnum;
        throw new IllegalArgumentException();
    }

    public static List<YesNoEnum> getWebList() {
        return Arrays.stream(YesNoEnum.values()).sorted(new YesNoEnumComparator()).toList();
    }

    public static List<PresentationElement> getWebListDoNotCare() {

        return Stream.concat(
                getWebList().stream().map(a -> new PresentationElement(a.name(), a.name(), a.getLabel())),
                Stream.of(new PresentationElement("doNotCare", "doNotCare", "label.yesno.donotcare"))
        ).toList();
    }

    public boolean booleanValue() {
        return this == Yes;
    }

    private static class YesNoEnumComparator implements java.util.Comparator<YesNoEnum> {
        @Override
        public int compare(YesNoEnum a, YesNoEnum b) {
            return a.getOrder() - b.getOrder();
        }
    }
}