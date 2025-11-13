package dwe.holding.supplyinventory.model;

import dwe.holding.admin.model.base.MemberBaseBO;
import dwe.holding.shared.model.converter.TaxedTypeEnumConverter;
import dwe.holding.shared.model.converter.YesNoEnumConverter;
import dwe.holding.shared.model.type.TaxedTypeEnum;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity(name = "SUPPLY_COSTING")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Costing extends MemberBaseBO {
    // TODO
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LOOKUPCOSTINGCATEGORY_ID", nullable = false)
    private LookupCostingCategory lookupCostingCategory;

    @Column(nullable = false)
    private String nomenclature;
    @Column(nullable = false)
    private Double sellExTaxPrice; //verkoop ex btw
    @Column(nullable = false)
    private Double purchasePrice; // inkoop prijs / distributor price
    @Column(nullable = false)
    private Double processingFee;

    private String distributor;
    private String distributorDescription;
    private String itemNumber;
    @Column(nullable = false)
    private Double quantityPerPackage;

    @Column(nullable = false)
    private Double uplift;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum hasBatchNr;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum hasSpillage;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = TaxedTypeEnumConverter.class)
    private TaxedTypeEnum taxed;

    private Short autoReminder;
    private String reminderNomenclature;
    private Short intervalInWeeks;
    private String rRemovePendingRemindersContaining;
    private String shortCode;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum deceasedPetPrompt;

    private String certificateManufacturer;
    private String certificateType;
    private String certificateSerialNumber;
    private String certificateVaccineExpires;
    @Lob
    private String instructions;

    private String prescriptionLabel;

    private Long supplies2Id;

    @Column(nullable = false)
    private Double supplies2IdIndyQtyDeduction;

    private Long barcode;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum deleted;
}