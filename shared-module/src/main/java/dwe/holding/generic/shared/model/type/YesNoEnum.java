package dwe.holding.generic.shared.model.type;


import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * Object representation of a Yes No object.
 * Can be overridden with the database information (language-specific) for this domain.
 */
@Getter
public enum YesNoEnum {

    Yes("Y", "label.yesno.yes", 1),
    No("N", "label.yesno.no", 2);

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
            throw new IllegalArgumentException();
        for (YesNoEnum anEnum : values())
            if (value.equalsIgnoreCase(anEnum.getDatabaseField())) return anEnum;
        throw new IllegalArgumentException();
    }

    public static YesNoEnum setEnum(String value) {
        if (value == null)
            throw new IllegalArgumentException();
        for (YesNoEnum anEnum : values())
            if (value.equalsIgnoreCase(anEnum.getDatabaseField())) return anEnum;
        throw new IllegalArgumentException();
    }

    public static List<YesNoEnum> getWebList() {
        return Arrays.stream(YesNoEnum.values()).sorted(new YesNoEnumComparator()).toList();
    }

    private static class YesNoEnumComparator implements java.util.Comparator<YesNoEnum> {
        @Override
        public int compare(YesNoEnum a, YesNoEnum b) {
            return a.getOrder() - b.getOrder();
        }
    }
}