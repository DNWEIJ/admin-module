package dwe.holding.supplyinventory.model.projection;

import dwe.holding.shared.model.type.TaxedTypeEnum;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.model.LookupCostingCategory;

public record CostingPriceProjection(
        Long id,
        String nomenclature,
        YesNoEnum hasBatchNr,
        YesNoEnum hasSpillage,
        Double processingFee,
        Double sellExTaxPrice,
        TaxedTypeEnum taxed,
        Double reductionPercentage,
        LookupCostingCategory lookupCostingCategory
) {
}