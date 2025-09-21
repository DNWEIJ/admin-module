package dwe.holding.generic.admin.model.type;


import java.util.Arrays;
import java.util.List;

public enum LanguagePrefEnum {

    Dutch("label.languagepref.netherlands", 1),
    English("label.languagepref.english", 2);

    private final String label;
    private final int order;

    LanguagePrefEnum(String label, int order) {
        this.order = order;
        this.label = label;
    }

    public static LanguagePrefEnum getEnum(String value) {
        if (value == null)
            throw new IllegalArgumentException();
        for (LanguagePrefEnum anEnum : LanguagePrefEnum.values())
            if (value.equalsIgnoreCase(anEnum.name())) return anEnum;
        throw new IllegalArgumentException();
    }

    public static List<LanguagePrefEnum> getWebList() {
        return Arrays.stream(LanguagePrefEnum.values()).sorted(new LanguagePrefEnumComparator()).toList();
    }

    public int getOrder() {
        return order;
    }

    public String getLabel() {
        return label;
    }

    private static class LanguagePrefEnumComparator implements java.util.Comparator<LanguagePrefEnum> {
        @Override
        public int compare(LanguagePrefEnum a, LanguagePrefEnum b) {
            return a.getOrder() - b.getOrder();
        }
    }

}