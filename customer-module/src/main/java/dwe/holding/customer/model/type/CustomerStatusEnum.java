package dwe.holding.customer.model.type;


import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum CustomerStatusEnum {

    NORMAL("N", "label.customerstatus.normal", 1),
    LATE_PAYMENT("L", "label.customerstatus.latepayment", 2),
    INCASSO("I", "label.customerstatus.incasso", 3),
    CLOSED("C", "label.customerstatus.closed", 4),
    INSURED("H", "label.customerstatus.insured", 5);

    private final String label;
    private final String databaseField;
    private final int order;

    CustomerStatusEnum(String databasesField, String label, int order) {
        this.databaseField = databasesField;
        this.order = order;
        this.label = label;
    }

    public static List<CustomerStatusEnum> getWebList() {
        return Arrays.stream(CustomerStatusEnum.values()).sorted(new CustomerStatusEnum.CustomerStatusEnumComparator()).toList();
    }

    public static CustomerStatusEnum getEnum(String value) {
        if (value == null)
            throw new IllegalArgumentException(value + " is not a valid CustomerStatusEnum");
        for (CustomerStatusEnum anEnum : values())
            if (value.equalsIgnoreCase(anEnum.getDatabaseField())) return anEnum;
        throw new IllegalArgumentException(value + " is not a valid CustomerStatusEnum");
    }

    private static class CustomerStatusEnumComparator implements java.util.Comparator<CustomerStatusEnum> {
        @Override
        public int compare(CustomerStatusEnum a, CustomerStatusEnum b) {
            return a.getOrder() - b.getOrder();
        }
    }
}