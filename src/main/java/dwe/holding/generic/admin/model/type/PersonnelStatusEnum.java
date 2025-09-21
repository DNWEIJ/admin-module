package dwe.holding.generic.admin.model.type;

import java.util.Arrays;
import java.util.List;

public enum PersonnelStatusEnum {
    Vet("label.personnelstatus.veterinarian", 1),
    Assistant("label.personnelstatus.assistant", 2),
    Other("label.personnelstatus.other", 3);

    private final String label;
    private final int order;

    PersonnelStatusEnum(String label, int order) {
        this.order = order;
        this.label = label;
    }

    public static PersonnelStatusEnum getEnum(String value) {
        if (value == null)
            throw new IllegalArgumentException();
        for (PersonnelStatusEnum anEnum : values())
            if (value.equalsIgnoreCase(anEnum.name())) return anEnum;
        throw new IllegalArgumentException();
    }

    public static List<PersonnelStatusEnum> getWebList() {
        return Arrays.stream(PersonnelStatusEnum.values()).sorted(new PersonnelStatusEnumComparator()).toList();
    }

    public int getOrder() {
        return order;
    }

    public String getLabel() {
        return label;
    }

    private static class PersonnelStatusEnumComparator implements java.util.Comparator<PersonnelStatusEnum> {
        @Override
        public int compare(PersonnelStatusEnum a, PersonnelStatusEnum b) {
            return a.getOrder() - b.getOrder();
        }
    }
}