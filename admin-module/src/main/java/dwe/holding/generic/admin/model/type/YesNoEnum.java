package dwe.holding.generic.admin.model.type;


import java.util.Arrays;
import java.util.List;

/**
 * Object representation of a Yes No object.
 * Can be overridden with he database information (language specific) for this domain.
 */

public enum YesNoEnum {

    Yes("label.yesno.yes", 1),
    No("label.yesno.no", 2);

    private final String label;
    private final int order;

    YesNoEnum(String label, int order) {
        this.order = order;
        this.label = label;
    }

    public static YesNoEnum getEnum(String value) {
        if (value == null)
            throw new IllegalArgumentException();
        for (YesNoEnum anEnum : values())
            if (value.equalsIgnoreCase(anEnum.name())) return anEnum;
        throw new IllegalArgumentException();
    }

    public static YesNoEnum setEnum(String value) {
        if (value == null)
            throw new IllegalArgumentException();
        for (YesNoEnum anEnum : values())
            if (value.equalsIgnoreCase(anEnum.name())) return anEnum;
        throw new IllegalArgumentException();
    }

    public static List<YesNoEnum> getWebList() {
        return Arrays.stream(YesNoEnum.values()).sorted(new YesNoEnumComparator()).toList();
    }

    public int getOrder() {
        return order;
    }

    public String getLabel() {
        return label;
    }

    private static class YesNoEnumComparator implements java.util.Comparator<YesNoEnum> {
        @Override
        public int compare(YesNoEnum a, YesNoEnum b) {
            return a.getOrder() - b.getOrder();
        }
    }
}