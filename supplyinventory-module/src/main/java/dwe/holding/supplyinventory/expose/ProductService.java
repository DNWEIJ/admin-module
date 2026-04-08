package dwe.holding.supplyinventory.expose;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.mapper.ProductMapper;
import dwe.holding.supplyinventory.model.*;
import dwe.holding.supplyinventory.model.projection.ProductPriceProjection;
import dwe.holding.supplyinventory.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ProductService {
    private final LookupProductCategoryRepository lookupProductCategoryRepository;
    private ProductRepository productRepository;
    private ProductGroupRepository productGroupRepository;
    private ProductPricePromotionRepository productPricePromotionRepository;
    private ProductMapper productMapper;
    private final ProductBatchNumberRepository productBatchNumberRepository;
    private final ProductSpillageRepository productSpillageRepository;
    private final ProductSpillageUsageRepository productSpillageUsageRepository;

    public List<ProductPriceProjection> getCorrectedPriceAndGroupingForProductId(Long costingId) {

        List<Product> listProductsInGroup = new ArrayList<>(findProductInGrouping(costingId));
        listProductsInGroup.add(productRepository.findById(costingId).orElseThrow());

        // validate if there are price promotions
        List<ProductPricePromotion> productPromotions = productPricePromotionRepository.findAllById(listProductsInGroup.stream().map(Product::getId).toList());
        if (productPromotions.isEmpty()) {
            return productMapper.toProjectionList(listProductsInGroup);
        }

        LocalDate today = LocalDate.now();
        // find active once's
        productPromotions = productPromotions.stream().filter(pricePromo ->
                (pricePromo.getStartDate().isBefore(today) && pricePromo.getEndDate() != null && pricePromo.getEndDate().isAfter(today))
        ).toList();

        if (productPromotions.isEmpty()) {
            return productMapper.toProjectionList(listProductsInGroup);
        }

        List<ProductPriceProjection> list = new ArrayList<>();
        // there acre active price promotions
        Map<Long, ProductPricePromotion> lookupMapOnCostingId =
                productPromotions.stream().collect(Collectors.toMap(ProductPricePromotion::getProductId, pricePromo -> pricePromo));

        listProductsInGroup.forEach(itCosting -> {
            ProductPricePromotion productPricePromotion = lookupMapOnCostingId.get(itCosting.getId());
            if (productPricePromotion != null) {
                list.add(productMapper.toProjection(itCosting, productPricePromotion));
            } else {
                list.add(productMapper.toProjection(itCosting));
            }

        });
        return list;
    }

    public void createBatchNumberIfNotExisting(Long id, String batchNumber) {
        Optional<ProductBatchNumber> cbnOptional = productBatchNumberRepository.findByProductIdAndMemberIdAndLocalMemberIdAndEndDateIsNullAndBatchNumber(id, AutorisationUtils.getCurrentUserMid(), AutorisationUtils.getCurrentUserMlid(), batchNumber);
        if (cbnOptional.isEmpty()) {
            productBatchNumberRepository.save(ProductBatchNumber.builder().productId(id).localMemberId(AutorisationUtils.getCurrentUserMlid()).batchNumber(batchNumber).build());
        }
    }

    public void createOrUpdateSpillage(Long productId, String spillageName, Long lineItemId) {
        Product product = productRepository.findByIdAndMemberId(productId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        ProductSpillage productSpillage = productSpillageRepository.findByNameAndMemberIdAndLocalMemberIdAndEndDateNotNull(spillageName, AutorisationUtils.getCurrentUserMid(), AutorisationUtils.getCurrentUserMlid());
        if (productSpillage == null) {
            productSpillage = new ProductSpillage();
            productSpillage.setProductId(product.getId());
            productSpillage.setStartDate(LocalDate.now());
            productSpillageRepository.save(productSpillage);
        }
        ProductSpillageUsage productSpillageUsage = new ProductSpillageUsage();
        productSpillageUsage.setLineItemId(lineItemId);
        productSpillageUsage.setProductSpillageId(productSpillage.getId());
        productSpillageUsageRepository.save(productSpillageUsage);
    }

    public List<Product> findProductInGrouping(Long productId) {
        return productRepository.findAllById(
                productGroupRepository.getCostingGroupsByParentProductId(productId)
                        .stream().map(ProductGroup::getChildProductId).collect(Collectors.toList())
        );
    }

    public Map<Long, BigDecimal> getGroupingsQuantity(Long productId) {
        return productGroupRepository.getCostingGroupsByParentProductId(productId).stream()
                .collect(Collectors.toMap(ProductGroup::getChildProductId, ProductGroup::getQuantity)
                );
    }

    public  List<PresentationElement>  getCategories() {
        return lookupProductCategoryRepository.findByDeletedOrderByCategoryName(YesNoEnum.No);
    }
}