package dwe.holding.generic.admin.model.type;


import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum LanguagePrefEnum {

    Dutch("nl","label.languagepref.netherlands", 1),
    English("en","label.languagepref.english", 2);

    private final String databaseField;
    private final String label;
    private final int order;

    LanguagePrefEnum(String databaseField, String label, int order) {
        this.databaseField = databaseField;
        this.order = order;
        this.label = label;
    }

    public static LanguagePrefEnum getEnum(String value) {
        if (value == null)
            throw new IllegalArgumentException();
        for (LanguagePrefEnum anEnum : LanguagePrefEnum.values())
            if (value.equalsIgnoreCase(anEnum.name())) return anEnum;
        throw new IllegalArgumentException(value + " is not a valid LanguagePrefEnum");
    }


    public static LanguagePrefEnum getEnumFromDbField(String value) {
        if (value == null)
            throw new IllegalArgumentException();
        for (LanguagePrefEnum anEnum : LanguagePrefEnum.values())
            if (value.equalsIgnoreCase(anEnum.getDatabaseField())) return anEnum;
        throw new IllegalArgumentException(value + " is not a valid LanguagePrefEnum");
    }

    public static List<LanguagePrefEnum> getWebList() {
        return Arrays.stream(LanguagePrefEnum.values()).sorted(new LanguagePrefEnumComparator()).toList();
    }

    private static class LanguagePrefEnumComparator implements java.util.Comparator<LanguagePrefEnum> {
        @Override
        public int compare(LanguagePrefEnum a, LanguagePrefEnum b) {
            return a.getOrder() - b.getOrder();
        }
    }

}