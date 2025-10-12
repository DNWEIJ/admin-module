package dwe.holding.generic.shared.model.type;

import java.util.Arrays;
import java.util.List;

public enum TaxedTypeEnum {
    NONE("label.taxedtype.none",1),
    GOOD("label.taxedtype.good",2),
    SERVICE("label.taxedtype.service",3);

    private final String label;
    private final int order;

    TaxedTypeEnum(String label, int order) {
        this.order = order;
        this.label = label;
    }


    public static List<TaxedTypeEnum> getWebList() {
        return Arrays.stream(TaxedTypeEnum.values()).sorted(new TaxedTypeEnum.TaxedTypeEnumComparator()).toList();
    }

    public int getOrder() {
        return order;
    }

    public String getLabel() {
        return label;
    }

    private static class TaxedTypeEnumComparator implements java.util.Comparator<TaxedTypeEnum> {
        @Override
        public int compare(TaxedTypeEnum a, TaxedTypeEnum b) {
            return a.getOrder() - b.getOrder();
        }
    }
}