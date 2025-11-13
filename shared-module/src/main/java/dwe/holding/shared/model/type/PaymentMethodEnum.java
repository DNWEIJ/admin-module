package dwe.holding.shared.model.type;

import java.util.Arrays;
import java.util.List;

public enum PaymentMethodEnum {
    CASH("0", "label.paymentmethod.cash", 2),
    CHECK("1", "label.paymentmethod.check", 8),
    CREDIT_CARD("2", "label.paymentmethod.credit_card", 7),
    OTHER("3", "label.paymentmethod.other", 6),
    CHIP("4", "label.paymentmethod.chip", 1),
    PIN("5", "label.paymentmethod.pin", 5),

    BANK("6", "label.paymentmethod.bank", 4),
    ONETIME("7", "label.paymentmethod.onetime", 3);

    private final String databaseField;
    private final String label;
    private final int order;


    PaymentMethodEnum(String databaseField, String label, int order) {
        this.databaseField = databaseField;
        this.order = order;
        this.label = label;
    }

    public static List<PaymentMethodEnum> getWebList() {
            return Arrays.stream(PaymentMethodEnum.values()).sorted(new PaymentMethodEnum.PaymentMethodEnumComparator()).toList();
    }

    public static PaymentMethodEnum getEnum(String value) {
        if (value == null)
            throw new IllegalArgumentException();
        for (PaymentMethodEnum anEnum : values())
            if (value.equalsIgnoreCase(anEnum.name())) return anEnum;
        throw new IllegalArgumentException();
    }


    public static PaymentMethodEnum getEnumFromDbField(String value) {
        if (value == null)
            throw new IllegalArgumentException();
        for (PaymentMethodEnum anEnum : values())
            if (value.equalsIgnoreCase(anEnum.getDatabaseField())) return anEnum;
        throw new IllegalArgumentException();
    }

    public int getOrder() {
        return order;
    }

    public String getLabel() {
        return label;
    }

    public String getDatabaseField() {
        return databaseField;
    }

    private static class PaymentMethodEnumComparator implements java.util.Comparator<PaymentMethodEnum> {
        @Override
        public int compare(PaymentMethodEnum a, PaymentMethodEnum b) {
            return a.getOrder() - b.getOrder();
        }
    }
}