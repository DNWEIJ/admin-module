package dwe.holding.supplyinventory.repository;

import dwe.holding.supplyinventory.model.Product;

public record ProductProjection(
        Product product,
        Long supplyId,
        Long pricePromotionId,
        boolean groupedProduct,
        boolean pricingProduct
) {
}