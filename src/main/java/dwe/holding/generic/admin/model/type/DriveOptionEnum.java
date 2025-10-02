package dwe.holding.generic.admin.model.type;

import java.util.Arrays;
import java.util.List;

public enum DriveOptionEnum {
    Not("label.driveoption.not", 1),
    Via("label.driveoption.via", 2),
    Direct("label.driveoption.direct", 3);

    private final String label;
    private final int order;

    DriveOptionEnum(String label, int order) {
        this.order = order;
        this.label = label;
    }

    public static DriveOptionEnum getEnum(String value) {
        if (value == null)
            throw new IllegalArgumentException();
        for (DriveOptionEnum anEnum : values())
            if (value.equalsIgnoreCase(anEnum.name())) return anEnum;
        throw new IllegalArgumentException();
    }

    public static List<DriveOptionEnum> getWebList() {
        return Arrays.stream(DriveOptionEnum.values()).sorted(new DriveOptionEnumComparator()).toList();
    }

    public int getOrder() {
        return order;
    }

    public String getLabel() {
        return label;
    }

    private static class DriveOptionEnumComparator implements java.util.Comparator<DriveOptionEnum> {
        @Override
        public int compare(DriveOptionEnum a, DriveOptionEnum b) {
            return a.getOrder() - b.getOrder();
        }
    }
}