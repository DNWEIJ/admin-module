package dwe.holding.admin.model.type;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum AgendaTypeEnum {

    Room("R", "label.agendatype.room", 1),
    Vet("V", "label.agendatype.vetenarian", 2),
    Week("W", "label.agendatype.week", 3);


    private final String databaseField;
    private final String label;
    private final int order;

    AgendaTypeEnum(String databaseField, String label, int order) {
        this.databaseField = databaseField;
        this.order = order;
        this.label = label;
    }

    public static AgendaTypeEnum getEnum(String value) {
        if (value == null)
            throw new IllegalArgumentException();
        for (AgendaTypeEnum anEnum : AgendaTypeEnum.values())
            if (value.equalsIgnoreCase(anEnum.name())) return anEnum;
        throw new IllegalArgumentException(value + " is not a valid AgendaTypeEnum");
    }


    public static AgendaTypeEnum getEnumFromDbField(String value) {
        if (value == null)
            throw new IllegalArgumentException();
        for (AgendaTypeEnum anEnum : AgendaTypeEnum.values())
            if (value.equalsIgnoreCase(anEnum.getDatabaseField())) return anEnum;
        throw new IllegalArgumentException(value + " is not a valid AgendaTypeEnum");
    }

    public static List<AgendaTypeEnum> getWebList() {
        return Arrays.stream(AgendaTypeEnum.values()).sorted(new AgendaTypeEnum.AgendaTypeEnumComparator()).toList();
    }

    private static class AgendaTypeEnumComparator implements java.util.Comparator<AgendaTypeEnum> {
        @Override
        public int compare(AgendaTypeEnum a, AgendaTypeEnum b) {
            return a.getOrder() - b.getOrder();
        }
    }
}