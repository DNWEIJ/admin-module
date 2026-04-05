package dwe.holding.supplyinventory.repository;

import dwe.holding.supplyinventory.model.Costing;

public record CostingProjection(
        Costing costing,
        Long supplyId,
        boolean groupedProduct,
        boolean pricingProduct
) {
}