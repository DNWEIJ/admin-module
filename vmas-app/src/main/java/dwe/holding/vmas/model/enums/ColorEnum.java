package dwe.holding.vmas.model.enums;


public enum ColorEnum {

    RED("red"),
    PINK("pink"),
    FUCHSIA("fuchsia"),
    PURPLE("purple"),
    VIOLET("violet"),
    INDIGO("indigo"),
    BLUE("blue"),
    AZURE("azure"),
    CYAN("cyan"),
    JADE("jade"),
    GREEN("green"),
    LIME("lime"),
    YELLOW("yellow"),
    AMBER("amber"),
    PUMPKIN("pumpkin"),
    ORANGE("orange"),
    SAND("sand"),
    GREY("grey");

    private final String value;

    ColorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ColorEnum fromValue(String value) {
        for (ColorEnum c : values()) {
            if (c.value.equalsIgnoreCase(value)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Unknown color: " + value);
    }
}