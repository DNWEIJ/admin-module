package dwe.holding.supplyinventory.repository;

import dwe.holding.supplyinventory.model.Costing;

public record CostingDto(
        Costing costing,
        boolean groupedProduct
) {
}