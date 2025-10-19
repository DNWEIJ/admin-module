package dwe.holding.generic.suppliesandinventory.model;

import dwe.holding.generic.admin.model.base.TenantBaseBO;
import dwe.holding.generic.shared.model.type.YesNoEnum;
import dwe.holding.generic.shared.model.type.TaxedTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Costing extends TenantBaseBO {
    // TODO
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "LOOKUPCOSTINGCATEGORY_ID", nullable = false)
//    private LookupCostingCategory lookupCostingCategory;


    @Column(nullable = false)
    private String nomenclature;

    @Column(nullable = false)
    private Double cost;

    @Column(nullable = false)
    private Double price;

    private String distributor;
    private String distDescription;
    private String itemNumber;

    @Column(nullable = false)
    private Double quantityPerPackage;

    @Column(nullable = false)
    private Double uplift;

    @Column(nullable = false)
    private String batch;

    @Column(nullable = false)
    private String spillage;

    @Column(nullable = false)
    private Double processingFee;

    @Column(nullable = false)
    private TaxedTypeEnum taxed;

    private Short autoReminder;
    private String reminderNomenclature;
    private Short interval;
    private String removePendingRemindersContaining;
    private String code;

    @Column(nullable = false)
    private Short deceasedPetPrompt;

    private String certificateManufacturer;
    private String certificateType;
    private String certificateSerialNumber;
    private String certificateVaccineExpires;
    private String instructions;
    private String prescriptionLabel;
    private Long supplies2Id;

    @Column(nullable = false)
    private Double supplies2IdIndyqtyDeduction;

    private Long barcode;

    @Column(nullable = false, length = 1)
    private YesNoEnum deleted;
}