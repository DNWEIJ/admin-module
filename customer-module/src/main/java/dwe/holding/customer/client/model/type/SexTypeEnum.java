package dwe.holding.customer.client.model.type;

import dwe.holding.shared.model.frontend.PresentationElement;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Getter
public enum SexTypeEnum {

    FEMALE("F", "label.petsexstatus.female", 1),
    FEMALESPAYED("S", "label.petsexstatus.female.spayed", 2),
    MALE("M", "label.petsexstatus.male", 3),
    MALENEUTERED("N", "label.petsexstatus.male.neutered", 4),
    UNKNOWN("U", "label.petsexstatus.unknown", 5),
    MALEPENISAMPUTATION("P", "label.petsexstatus.penisamputation", 6),
    MALEVASECTOMIE("V", "label.petsexstatus.vasectomie", 7);

    private final String databaseField;
    private final String label;
    private final int order;

    SexTypeEnum(String databasesField, String label, int order) {
        this.databaseField = databasesField;
        this.order = order;
        this.label = label;
    }

    public static List<SexTypeEnum> getWebList() {
        return Arrays.stream(SexTypeEnum.values()).sorted(new SexTypeEnum.SexTypeEnumComparator()).toList();
    }

    public static List<PresentationElement> getWebListDoNotCare() {
        return Stream.concat(
                getWebList().stream().map(a -> new PresentationElement(a.name(), a.name(), a.getLabel())),
                Stream.of(new PresentationElement("doNotCare", "doNotCare", "label.yesno.donotcare"))
        ).toList();
    }

    public static SexTypeEnum getEnum(String value) {
        if (value == null)
            throw new IllegalArgumentException(value + " is not a valid CustomerStatusEnum");
        for (SexTypeEnum anEnum : values())
            if (value.equalsIgnoreCase(anEnum.name())) return anEnum;
        throw new IllegalArgumentException(value + " is not a valid CustomerStatusEnum");
    }

    public static SexTypeEnum getEnumFromDbField(String value) {
        if (value == null)
            throw new IllegalArgumentException(value + " is not a valid CustomerStatusEnum");
        for (SexTypeEnum anEnum : values())
            if (value.equalsIgnoreCase(anEnum.getDatabaseField())) return anEnum;
        throw new IllegalArgumentException(value + " is not a valid CustomerStatusEnum");
    }

    private static class SexTypeEnumComparator implements java.util.Comparator<SexTypeEnum> {
        @Override
        public int compare(SexTypeEnum a, SexTypeEnum b) {
            return a.getOrder() - b.getOrder();
        }
    }
}