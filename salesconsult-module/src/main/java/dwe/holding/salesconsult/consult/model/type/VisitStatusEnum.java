package dwe.holding.salesconsult.consult.model.type;

import lombok.Getter;

import java.util.*;

@Getter
public enum VisitStatusEnum {

    PLANNED("X", "label.visit.status.planned", 0, "#99CCFF"),
    WAITING("W", "label.visit.status.waiting", 1,"#00B8FF"),
    CONSULT("C", "label.visit.status.consult", 2, "#00FF00"),
    INTAKE("I", "label.visit.status.intake", 3, "#00CCCC"),
    OPERATION("O", "label.visit.status.operation", 4,"#00AE00"),
    RECOVERY("R", "label.visit.status.recovery", 5,"#E6E64C"),
    FINISHED_CONSULT("F", "label.visit.status.ready_for_checkout", 6, "#FFFF00"),
    PAYMENT("P", "label.visit.status.ready_to_pay", 7,"#FF950E"),
    FINISHED("D", "label.visit.status.finished", 8,"#FF3333");


    private final String databaseField;
    private final String label;
    private final int order;
    private final String color;

    private static final Map<VisitStatusEnum, Set<VisitStatusEnum>> NEXT = new EnumMap<>(VisitStatusEnum.class);
    private static final Map<VisitStatusEnum, VisitStatusEnum> PREVIOUS = new EnumMap<>(VisitStatusEnum.class);

    VisitStatusEnum(String databaseField, String label, int order,  String color) {
        this.databaseField = databaseField;
        this.label = label;
        this.order = order;
        this.color = color;
    }

    static {
        for (VisitStatusEnum s : values()) {
            NEXT.put(s, EnumSet.noneOf(VisitStatusEnum.class));
        }

        // flow definition
        link(PLANNED, WAITING);

        link(WAITING, CONSULT);
        link(WAITING, INTAKE);

        link(CONSULT, FINISHED_CONSULT);

        link(INTAKE, OPERATION);
        link(OPERATION, RECOVERY);
        link(RECOVERY, FINISHED_CONSULT);

        link(FINISHED_CONSULT, PAYMENT);
        link(PAYMENT, FINISHED);
    }

    private static void link(VisitStatusEnum from, VisitStatusEnum to) {
        NEXT.get(from).add(to);
        PREVIOUS.put(to, from); // valid because each node has only one previous in this model
    }

    public Set<VisitStatusEnum> nextOptions() {
        return Collections.unmodifiableSet(NEXT.get(this));
    }

    public Optional<VisitStatusEnum> previous() {
        return Optional.ofNullable(PREVIOUS.get(this));
    }

    public static java.util.List<VisitStatusEnum> getWebList() {
        return java.util.Arrays.stream(VisitStatusEnum.values())
                .sorted(java.util.Comparator.comparingInt(VisitStatusEnum::getOrder))
                .toList();
    }

    public static VisitStatusEnum getEnumFromDbField(String value) {
        if (value == null)
            throw new IllegalArgumentException(value + " is not a valid VisitStatusEnum");
        for (VisitStatusEnum anEnum : values())
            if (value.equalsIgnoreCase(anEnum.getDatabaseField())) return anEnum;
        throw new IllegalArgumentException(value + " is not a valid VisitStatusEnum");
    }

    public static boolean isOpen(VisitStatusEnum status) {
        return !status.equals(VisitStatusEnum.FINISHED);
    }

    public static boolean isClosed(VisitStatusEnum status) {
        return !isOpen(status);
    }
}