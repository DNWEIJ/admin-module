package dwe.holding.supplyinventory.model.type;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * Object representation of a Yes No object.
 * Can be overridden with the database information (language-specific) for this domain.
 * <p>
 * This enum is also used in preferences settings that are stored as json (userPreferences localMemberPreferences etc) hence the json annotations
 */
@Getter
public enum PricingTypeEnum {

    SALESPRICE_PROCESINGFEE("S", "label.pricingtype.salesprice"),
    REDUCTION_PERCENTAGE("R", "label.pricingtype.percentage"),
    TWO_FOR_THREE("2", "label.pricingtype.3v2");

    @JsonValue
    private final String databaseField;
    private final String label;

    PricingTypeEnum(String databaseField, String label) {
        this.databaseField = databaseField;
        this.label = label;
    }

    public static PricingTypeEnum getEnum(String value) {
        if (value == null)
            throw new IllegalArgumentException("Null value for PricingTypeEnum");
        for (PricingTypeEnum anEnum : values())
            if (value.equalsIgnoreCase(anEnum.name())) return anEnum;
        throw new IllegalArgumentException();
    }

    @JsonCreator
    public static PricingTypeEnum getEnumFromDbField(String value) {
        if (value == null)
            throw new IllegalArgumentException("Null value in db for PricingTypeEnum");
        for (PricingTypeEnum anEnum : values())
            if (value.equalsIgnoreCase(anEnum.getDatabaseField())) return anEnum;
        throw new IllegalArgumentException();
    }

    public static List<PricingTypeEnum> getWebList() {
        return Arrays.stream(PricingTypeEnum.values()).toList();
    }
}