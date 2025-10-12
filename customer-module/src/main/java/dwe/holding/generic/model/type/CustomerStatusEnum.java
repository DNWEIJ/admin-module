package dwe.holding.generic.model.type;


import java.util.Arrays;
import java.util.List;

public enum CustomerStatusEnum {

    NORMAL("label.customerstatus.normal", 1),
    LATE_PAYMENT("label.customerstatus.latepayment", 2),
    INCASSO("label.customerstatus.incasso", 3),
    CLOSED("label.customerstatus.closed", 4),
    INSURED("label.customerstatus.insured", 5);

    private final String label;
    private final int order;

    CustomerStatusEnum(String label, int order) {
        this.order = order;
        this.label = label;
    }

    public static List<CustomerStatusEnum> getWebList() {
        return Arrays.stream(CustomerStatusEnum.values()).sorted(new CustomerStatusEnum.CustomerStatusEnumComparator()).toList();
    }

    public int getOrder() {
        return order;
    }

    public String getLabel() {
        return label;
    }

    private static class CustomerStatusEnumComparator implements java.util.Comparator<CustomerStatusEnum> {
        @Override
        public int compare(CustomerStatusEnum a, CustomerStatusEnum b) {
            return a.getOrder() - b.getOrder();
        }
    }
}