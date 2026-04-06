package dwe.holding.supplyinventory.model.projection;

import dwe.holding.shared.model.type.TaxedTypeEnum;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.model.LookupProductCategory;

import java.math.BigDecimal;

public record ProductPriceProjection(
        Long id,
        String nomenclature,
        YesNoEnum hasBatchNr,
        YesNoEnum hasSpillage,
        BigDecimal processingFeeExTax,
        BigDecimal salesPriceExTax,
        TaxedTypeEnum taxed,
        BigDecimal reductionPercentage,
        LookupProductCategory lookupProductCategory,
        String prescriptionLabel,
        YesNoEnum autoReminder,
        String rRemovePendingRemindersContaining,
        String reminderNomenclature,
        Short intervalInWeeks,
        YesNoEnum deceasedPetPrompt,
        Long supplyId
) {
}