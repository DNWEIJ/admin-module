package dwe.holding.admin.model.type;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum PersonnelStatusEnum {
    Vet("V","label.personnelstatus.veterinarian", 1),
    Assistant("A","label.personnelstatus.assistant", 2),
    Other("O","label.personnelstatus.other", 3);

    private final String databaseField;
    private final String label;
    private final int order;

    PersonnelStatusEnum(String databaseField, String label, int order) {
        this.databaseField = databaseField;
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
    public static PersonnelStatusEnum getEnumFromDbField(String value) {
        if (value == null)
            throw new IllegalArgumentException();
        for (PersonnelStatusEnum anEnum : values())
            if (value.equalsIgnoreCase(anEnum.getDatabaseField())) return anEnum;
        throw new IllegalArgumentException();
    }
    public static List<PersonnelStatusEnum> getWebList() {
        return Arrays.stream(PersonnelStatusEnum.values()).sorted(new PersonnelStatusEnumComparator()).toList();
    }

    private static class PersonnelStatusEnumComparator implements java.util.Comparator<PersonnelStatusEnum> {
        @Override
        public int compare(PersonnelStatusEnum a, PersonnelStatusEnum b) {
            return a.getOrder() - b.getOrder();
        }
    }
}