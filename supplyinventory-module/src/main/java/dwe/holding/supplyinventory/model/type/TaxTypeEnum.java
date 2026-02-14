package dwe.holding.supplyinventory.model.type;

import lombok.Getter;

@Getter
public enum TaxTypeEnum {

    NO_TAX("0", "label.taxedtype.none", 0),
    LOW("1", "label.taxedtype.good", 1),
    HIGH("2", "label.taxedtype.service", 2);

    private final String databaseField;
    private final String label;
    private final int order;

    TaxTypeEnum(String databaseField, String label, int order) {
        this.databaseField = databaseField;
        this.label = label;
        this.order = order;
    }

    public static java.util.List<TaxTypeEnum> getWebList() {
        return java.util.Arrays.stream(TaxTypeEnum.values())
                .sorted(java.util.Comparator.comparingInt(TaxTypeEnum::getOrder))
                .toList();
    }

    public static TaxTypeEnum getEnumFromDbField(String value) {
        if (value == null)
            throw new IllegalArgumentException(value + " is not a valid TaxTypeEnum");
        for (TaxTypeEnum anEnum : values())
            if (value.equalsIgnoreCase(anEnum.getDatabaseField())) return anEnum;
        throw new IllegalArgumentException(value + " is not a valid TaxTypeEnum");
    }
}
