package dwe.holding.supplyinventory.model.projection;

import dwe.holding.shared.model.type.TaxedTypeEnum;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.model.LookupCostingCategory;

import java.math.BigDecimal;

public record CostingPriceProjection(
        Long id,
        String nomenclature,
        YesNoEnum hasBatchNr,
        YesNoEnum hasSpillage,
        BigDecimal processingFee,
        BigDecimal sellExTaxPrice,
        TaxedTypeEnum taxed,
        BigDecimal reductionPercentage,
        LookupCostingCategory lookupCostingCategory,
        String prescriptionLabel
) {
}