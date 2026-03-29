package dwe.holding.reporting;

import lombok.Getter;

@Getter
public enum PaymentListTypeEnum {
    STANDARD("label.payment.status.standard"),
    OUT("label.payment.status.out"),
    OVER("label.payment.status.over");

    private final String label;

    PaymentListTypeEnum(String label) {
        this.label = label;
    }

    public static java.util.List<PaymentListTypeEnum> getWebList() {
        return java.util.Arrays.stream(PaymentListTypeEnum.values())
                .toList();
    }
}
