package dwe.holding.salesconsult.consult.model.type;

import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Getter
public enum InvoiceStatusEnum {

    NEW("0", "label.invoicestatus.new", 1),
    FIRST_SEND("1", "label.invoicestatus.first_send", 2),
    SECOND_SEND("2", "label.invoicestatus.second_send", 3),
    THIRD_SEND("3", "label.invoicestatus.third_send", 4),
    FINISHED("4", "label.invoicestatus.finished", 5),
    ALL("5", "label.invoicestatus.all", 6);

    private final String databaseField;
    private final String label;
    private final int order;

    InvoiceStatusEnum(String databaseField, String label, int order) {
        this.databaseField = databaseField;
        this.label = label;
        this.order = order;
    }

    public static List<InvoiceStatusEnum> getWebList() {
        return Arrays.stream(InvoiceStatusEnum.values())
                .sorted(new InvoiceStatusEnumComparator())
                .toList();
    }

    public static InvoiceStatusEnum getEnum(String value) {
        if (value == null)
            throw new IllegalArgumentException(value + " is not a valid InvoiceStatusEnum");
        for (InvoiceStatusEnum anEnum : values())
            if (value.equalsIgnoreCase(anEnum.name())) return anEnum;
        throw new IllegalArgumentException(value + " is not a valid InvoiceStatusEnum");
    }

    public static InvoiceStatusEnum getEnumFromDbField(String value) {
        if (value == null)
            throw new IllegalArgumentException(value + " is not a valid InvoiceStatusEnum");
        for (InvoiceStatusEnum anEnum : values())
            if (value.equalsIgnoreCase(anEnum.getDatabaseField())) return anEnum;
        throw new IllegalArgumentException(value + " is not a valid InvoiceStatusEnum");
    }

    private static class InvoiceStatusEnumComparator implements Comparator<InvoiceStatusEnum> {
        @Override
        public int compare(InvoiceStatusEnum a, InvoiceStatusEnum b) {
            return a.getOrder() - b.getOrder();
        }
    }
}