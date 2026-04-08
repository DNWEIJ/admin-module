package dwe.holding.supplyinventory.mapper;

import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.model.Product;
import dwe.holding.supplyinventory.model.ProductPricePromotion;
import dwe.holding.supplyinventory.model.projection.ProductPriceProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "reductionPercentage", constant = "0.0")
    ProductPriceProjection toProjection(Product product);

    List<ProductPriceProjection> toProjectionList(List<Product> products);

    default boolean map(YesNoEnum value) {
        return value == YesNoEnum.Yes;
    }

    default YesNoEnum map(boolean value) {
        return value ? YesNoEnum.Yes : YesNoEnum.No;
    }

    default ProductPriceProjection toProjection(Product product, ProductPricePromotion productPricePromotion) {
        return new ProductPriceProjection(
                product.getId(),
                product.getNomenclature(),
                product.getHasBatchNr(),
                product.getHasSpillage(),
                product.getProcessingFeeExTax(),
                productPricePromotion.getSalesPriceExTax(),
                product.getTaxed(),
                productPricePromotion.getReductionPercentage(),
                product.getLookupProductCategory(),
                product.getPrescriptionLabel(),
                product.getAutoReminder(),
                product.getRRemovePendingRemindersContaining(),
                product.getReminderNomenclature(),
                product.getIntervalInWeeks(),
                product.getDeceasedPetPrompt(),
                product.getSupply() == null? -1L: product.getSupply().getId()
        );
    }

    @Mappings({
            @Mapping(target = "memberId", ignore = true),
            @Mapping(target = "version", ignore = true),
            @Mapping(target = "id", ignore = true)
    })
    Product fromForm(@MappingTarget Product entity, Product form);
}