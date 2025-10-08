package dwe.holding.generic.model.type;

public enum SexTypeEnum {

    FEMALE("label.patientsexstatus.female",1),
    FEMALESPAYED("label.patientsexstatus.female_spayed",2),
    MALE("label.patientsexstatus.male",3),
    MALENEUTERED("label.patientsexstatus.male_neutered",4),
    UNKNOWN("label.patientsexstatus.unknown",5),
    MALEPENISAMPUTATION("label.patientsexstatus.male_penisamputation",6),
    MALEVASECTOMIE("label.patientsexstatus.male_vasectomie",7);
    private final String label;
    private final int order;

    SexTypeEnum(String label, int order) {
        this.order = order;
        this.label = label;
    }
}