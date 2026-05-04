package dwe.holding.reporting;

import lombok.Getter;

@Getter
public enum PaymentListTypeEnum {
    STANDARD("label.payment.status.standard"),
    OUT("label.payment.status.out"),
    OVER("label.payment.status.over"),
    AR("label.payment.status.AR");
    private final String label;

    PaymentListTypeEnum(String label) {
        this.label = label;
    }

    public static java.util.List<PaymentListTypeEnum> getWebList() {
        return java.util.Arrays.stream(PaymentListTypeEnum.values())
                .toList();
    }

    public boolean isStandard(){
        return this == STANDARD;
    }
    public boolean isOut(){
        return this == OUT;
    }
    public boolean isOver(){
        return this == OVER;
    }
    public boolean isAR(){ return this == AR; }
}
