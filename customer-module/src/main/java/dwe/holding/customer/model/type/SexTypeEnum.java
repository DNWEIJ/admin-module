package dwe.holding.customer.model.type;

import java.util.Arrays;
import java.util.List;

public enum SexTypeEnum {

    FEMALE("label.patientsexstatus.female", 1),
    FEMALESPAYED("label.patientsexstatus.female_spayed", 2),
    MALE("label.patientsexstatus.male", 3),
    MALENEUTERED("label.patientsexstatus.male_neutered", 4),
    UNKNOWN("label.patientsexstatus.unknown", 5),
    MALEPENISAMPUTATION("label.patientsexstatus.male_penisamputation", 6),
    MALEVASECTOMIE("label.patientsexstatus.male_vasectomie", 7);

    private final String label;
    private final int order;

    SexTypeEnum(String label, int order) {
        this.order = order;
        this.label = label;
    }

    public static List<SexTypeEnum> getWebList() {
        return Arrays.stream(SexTypeEnum.values()).sorted(new SexTypeEnum.SexTypeEnumComparator()).toList();
    }

    public int getOrder() {
        return order;
    }

    public String getLabel() {
        return label;
    }

    private static class SexTypeEnumComparator implements java.util.Comparator<SexTypeEnum> {
        @Override
        public int compare(SexTypeEnum a, SexTypeEnum b) {
            return a.getOrder() - b.getOrder();
        }
    }
}