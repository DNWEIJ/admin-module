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

    public List<ProductPriceProjection> getCorrectedPriceAndGroupingForCostingId(Long costingId) {

        List<Product> listCostingsInGroup = new ArrayList<>(findCostingOnGrouping(costingId));
        listCostingsInGroup.add(productRepository.findById(costingId).orElseThrow());

        // validate if there are price promotions
        List<ProductPricePromotion> costingPromotions = productPricePromotionRepository.findAllById(listCostingsInGroup.stream().map(Product::getId).toList());
        if (costingPromotions.isEmpty()) {
            return productMapper.toProjectionList(listCostingsInGroup);
        }

        LocalDate today = LocalDate.now();
        // find active once's
        costingPromotions = costingPromotions.stream().filter(pricePromo ->
                (pricePromo.getStartDate().isBefore(today) && pricePromo.getEndDate() != null && pricePromo.getEndDate().isAfter(today))
        ).toList();

        if (costingPromotions.isEmpty()) {
            return productMapper.toProjectionList(listCostingsInGroup);
        }

        List<ProductPriceProjection> list = new ArrayList<>();
        // there acre active price promotions
        Map<Long, ProductPricePromotion> lookupMapOnCostingId =
                costingPromotions.stream().collect(Collectors.toMap(ProductPricePromotion::getCostingId, pricePromo -> pricePromo));

        listCostingsInGroup.forEach(itCosting -> {
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
        Optional<ProductBatchNumber> cbnOptional = productBatchNumberRepository.findByCostingIdAndMemberIdAndLocalMemberIdAndEndDateIsNullAndBatchNumber(id, AutorisationUtils.getCurrentUserMid(), AutorisationUtils.getCurrentUserMlid(), batchNumber);
        if (cbnOptional.isEmpty()) {
            productBatchNumberRepository.save(ProductBatchNumber.builder().costingId(id).localMemberId(AutorisationUtils.getCurrentUserMlid()).batchNumber(batchNumber).build());
        }
    }

    public void createOrUpdateSpillage(Long costingId, String spillageName, Long lineItemId) {
        Product product = productRepository.findByIdAndMemberId(costingId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        ProductSpillage productSpillage = productSpillageRepository.findByNameAndMemberIdAndLocalMemberIdAndEndDateNotNull(spillageName, AutorisationUtils.getCurrentUserMid(), AutorisationUtils.getCurrentUserMlid());
        if (productSpillage == null) {
            productSpillage = new ProductSpillage();
            productSpillage.setCostingId(product.getId());
            productSpillage.setStartDate(LocalDate.now());
            productSpillageRepository.save(productSpillage);
        }
        ProductSpillageUsage productSpillageUsage = new ProductSpillageUsage();
        productSpillageUsage.setLineItemId(lineItemId);
        productSpillageUsage.setCostingSpillageId(productSpillage.getId());
        productSpillageUsageRepository.save(productSpillageUsage);
    }

    public List<Product> findCostingOnGrouping(Long costingId) {
        return productRepository.findAllById(
                productGroupRepository.getCostingGroupsByParentCostingId(costingId)
                        .stream().map(ProductGroup::getChildCostingId).collect(Collectors.toList())
        );
    }

    public Map<Long, BigDecimal> getGroupingsQuantity(Long costingId) {
        return productGroupRepository.getCostingGroupsByParentCostingId(costingId).stream()
                .collect(Collectors.toMap(ProductGroup::getChildCostingId, ProductGroup::getQuantity)
                );
    }

    public  List<PresentationElement>  getCategories() {
        return lookupProductCategoryRepository.findByDeletedOrderByCategoryName(YesNoEnum.No);
    }
}