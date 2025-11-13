package dwe.holding.supplyinventory.model.projection;

import dwe.holding.shared.model.type.YesNoEnum;

public record CostingProjection(Long id, String nomenclature, YesNoEnum hasBatchNr) {
}