package dwe.holding.salesconsult.consult.model.type;

import lombok.Getter;

@Getter
public enum VisitStatusEnum {

    PLANNED("X", "label.visitstatus.planned", 0),
    WAITING("W", "label.visitstatus.waiting", 1),
    CONSULT("C", "label.visitstatus.consult", 2),
    INTAKE("I", "label.visitstatus.intake", 3),
    OPERATION("O", "label.visitstatus.operation", 4),
    RECOVERY("R", "label.visitstatus.recovery", 5),
    FINISHED_CONSULT("F", "label.visitstatus.finished_consult", 6),
    PAYMENT("P", "label.visitstatus.payment", 7),
    FINISHED("D", "label.visitstatus.finished", 8);

    private final String databaseField;
    private final String label;
    private final int order;

    VisitStatusEnum(String databaseField, String label, int order) {
        this.databaseField = databaseField;
        this.label = label;
        this.order = order;
    }


    public static java.util.List<VisitStatusEnum> getWebList() {
        return java.util.Arrays.stream(VisitStatusEnum.values())
                .sorted(java.util.Comparator.comparingInt(VisitStatusEnum::getOrder))
                .toList();
    }

    public static VisitStatusEnum getEnum(String value) {
        if (value == null)
            throw new IllegalArgumentException(value + " is not a valid VisitStatusEnum");
        for (VisitStatusEnum anEnum : values())
            if (value.equalsIgnoreCase(anEnum.name())) return anEnum;
        throw new IllegalArgumentException(value + " is not a valid VisitStatusEnum");
    }

    public static VisitStatusEnum getEnumFromDbField(String value) {
        if (value == null)
            throw new IllegalArgumentException(value + " is not a valid VisitStatusEnum");
        for (VisitStatusEnum anEnum : values())
            if (value.equalsIgnoreCase(anEnum.getDatabaseField())) return anEnum;
        throw new IllegalArgumentException(value + " is not a valid VisitStatusEnum");
    }

    public static boolean isOpen(VisitStatusEnum status) {
        return status.equals(VisitStatusEnum.PAYMENT) || status.equals(VisitStatusEnum.FINISHED);
    }
    public static boolean isClosed(VisitStatusEnum status) {
        return !isOpen(status);
    }
}