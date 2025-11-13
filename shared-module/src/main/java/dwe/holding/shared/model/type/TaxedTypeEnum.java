package dwe.holding.shared.model.type;

import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Getter
public enum TaxedTypeEnum {

    NONE("0", "label.taxedtype.none", 0),
    GOOD("1", "label.taxedtype.good", 1),
    SERVICE("2", "label.taxedtype.service", 2);

    private final String databaseField;
    private final String label;
    private final int order;

    TaxedTypeEnum(String databaseField, String label, int order) {
        this.databaseField = databaseField;
        this.label = label;
        this.order = order;
    }

    public static List<TaxedTypeEnum> getWebList() {
        return Arrays.stream(TaxedTypeEnum.values())
                .sorted(Comparator.comparingInt(TaxedTypeEnum::getOrder))
                .toList();
    }

    public static TaxedTypeEnum getEnum(String value) {
        if (value == null)
            throw new IllegalArgumentException(value + " is not a valid TaxedTypeEnum");
        for (TaxedTypeEnum anEnum : values())
            if (value.equalsIgnoreCase(anEnum.name())) return anEnum;
        throw new IllegalArgumentException(value + " is not a valid TaxedTypeEnum");
    }

    public static TaxedTypeEnum getEnumFromDbField(String value) {
        if (value == null)
            throw new IllegalArgumentException(value + " is not a valid TaxedTypeEnum");
        for (TaxedTypeEnum anEnum : values())
            if (value.equalsIgnoreCase(anEnum.getDatabaseField())) return anEnum;
        throw new IllegalArgumentException(value + " is not a valid TaxedTypeEnum");
    }
}