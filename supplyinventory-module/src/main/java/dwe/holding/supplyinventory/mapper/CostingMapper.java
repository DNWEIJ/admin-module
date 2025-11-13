package dwe.holding.supplyinventory.mapper;

import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.model.Costing;
import dwe.holding.supplyinventory.model.CostingPricePromotion;
import dwe.holding.supplyinventory.model.projection.CostingPriceProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CostingMapper {
    @Mapping(target = "reductionPercentage", constant = "0.0")
    CostingPriceProjection toProjection(Costing costing);

    List<CostingPriceProjection> toProjectionList(List<Costing> costings);

    default boolean map(YesNoEnum value) {
        return value == YesNoEnum.Yes;
    }

    default YesNoEnum map(boolean value) {
        return value ? YesNoEnum.Yes : YesNoEnum.No;
    }

    default CostingPriceProjection toProjection(Costing costing, CostingPricePromotion costingPricePromotion) {
        return new CostingPriceProjection(
                costing.getId(),
                costing.getNomenclature(),
                costing.getHasBatchNr(),
                costing.getHasSpillage(),
                costing.getProcessingFee(),
                costingPricePromotion.getSellExTaxPrice(),
                costing.getTaxed(),
                costingPricePromotion.getReductionPercentage(),
                costing.getLookupCostingCategory()
        );
    }
}