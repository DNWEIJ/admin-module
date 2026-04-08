package dwe.holding.supplyinventory.model.projection;

import dwe.holding.shared.model.type.YesNoEnum;

public record ProductProjection(Long id, String nomenclature, YesNoEnum hasBatchNr) {
}