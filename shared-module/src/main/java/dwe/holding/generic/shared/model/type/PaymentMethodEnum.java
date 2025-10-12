package dwe.holding.generic.shared.model.type;

import java.util.Arrays;
import java.util.List;

public enum PaymentMethodEnum {
    CASH("label.paymentmethod.cash", 1),
    CHECK("label.paymentmethod.check", 2),
    CREDIT_CARD("label.paymentmethod.credit_card", 3),
    CHIP("label.paymentmethod.chip", 4),
    PIN("label.paymentmethod.pin", 5),
    OTHER("label.paymentmethod.other", 6),
    BANK("label.paymentmethod.bank", 7),
    ONETIME("label.paymentmethod.onetime", 8);

    private final String label;
    private final int order;

    PaymentMethodEnum(String label, int order) {
        this.order = order;
        this.label = label;
    }

    public static List<PaymentMethodEnum> getWebList() {
        return Arrays.stream(PaymentMethodEnum.values()).sorted(new PaymentMethodEnum.PaymentMethodEnumComparator()).toList();
    }

    public int getOrder() {
        return order;
    }

    public String getLabel() {
        return label;
    }

    private static class PaymentMethodEnumComparator implements java.util.Comparator<PaymentMethodEnum> {
        @Override
        public int compare(PaymentMethodEnum a, PaymentMethodEnum b) {
            return a.getOrder() - b.getOrder();
        }
    }
}