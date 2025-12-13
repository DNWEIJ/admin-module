package dwe.holding.supplyinventory.expose;

import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.supplyinventory.mapper.CostingMapper;
import dwe.holding.supplyinventory.model.*;
import dwe.holding.supplyinventory.model.projection.CostingPriceProjection;
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
public class CostingService {
    private final LookupCostingCategoryRepository lookupCostingCategoryRepository;
    private CostingRepository costingRepository;
    private CostingGroupRepository costingGroupRepository;
    private CostingPricePromotionRepository costingPricePromotionRepository;
    private CostingMapper costingMapper;
    private final CostingBatchNumberRepository costingBatchNumberRepository;
    private final CostingSpillageRepository costingSpillageRepository;
    private final CostingSpillageUsageRepository costingSpillageUsageRepository;

    public List<CostingPriceProjection> getCorrectedPriceAndGroupingForCostingId(Long costingId) {

        List<Costing> listCostingsInGroup = new ArrayList<>(findCostingOnGrouping(costingId));
        listCostingsInGroup.add(costingRepository.findById(costingId).orElseThrow());

        // validate if there are price promotions
        List<CostingPricePromotion> costingPromotions = costingPricePromotionRepository.findAllById(listCostingsInGroup.stream().map(Costing::getId).toList());
        if (costingPromotions.isEmpty()) {
            return costingMapper.toProjectionList(listCostingsInGroup);
        }

        LocalDate today = LocalDate.now();
        // find active once's
        costingPromotions = costingPromotions.stream().filter(pricePromo ->
                (pricePromo.getStartDate().isBefore(today) && pricePromo.getEndDate() != null && pricePromo.getEndDate().isAfter(today))
        ).toList();

        if (costingPromotions.isEmpty()) {
            return costingMapper.toProjectionList(listCostingsInGroup);
        }

        List<CostingPriceProjection> list = new ArrayList<>();
        // there acre active price promotions
        Map<Long, CostingPricePromotion> lookupMapOnCostingId =
                costingPromotions.stream().collect(Collectors.toMap(CostingPricePromotion::getCostingId, pricePromo -> pricePromo));

        listCostingsInGroup.forEach(itCosting -> {
            CostingPricePromotion costingPricePromotion = lookupMapOnCostingId.get(itCosting.getId());
            if (costingPricePromotion != null) {
                list.add(costingMapper.toProjection(itCosting, costingPricePromotion));
            } else {
                list.add(costingMapper.toProjection(itCosting));
            }

        });
        return list;
    }

    public void createBatchNumberIfNotExisting(Long id, String batchNumber) {
        Optional<CostingBatchNumber> cbnOptional = costingBatchNumberRepository.findByCostingIdAndMemberIdAndLocalMemberIdAndEndDateIsNullAndBatchNumber(id, AutorisationUtils.getCurrentUserMid(), AutorisationUtils.getCurrentUserMlid(), batchNumber);
        if (cbnOptional.isEmpty()) {
            costingBatchNumberRepository.save(CostingBatchNumber.builder().costingId(id).localMemberId(AutorisationUtils.getCurrentUserMlid()).batchNumber(batchNumber).build());
        }
    }

    public void createOrUpdateSpillage(Long costingId, String spillageName, Long lineItemId) {
        Costing costing = costingRepository.findByIdAndMemberId(costingId, AutorisationUtils.getCurrentUserMid()).orElseThrow();
        CostingSpillage costingSpillage = costingSpillageRepository.findByNameAndMemberIdAndLocalMemberIdAndEndDateNotNull(spillageName, AutorisationUtils.getCurrentUserMid(), AutorisationUtils.getCurrentUserMlid());
        if (costingSpillage == null) {
            costingSpillage = new CostingSpillage();
            costingSpillage.setCostingId(costing.getId());
            costingSpillage.setPackageAmount(costing.getQuantityPerPackage());
            costingSpillage.setStartDate(LocalDate.now());
            costingSpillageRepository.save(costingSpillage);
        }
        CostingSpillageUsage costingSpillageUsage = new CostingSpillageUsage();
        costingSpillageUsage.setLineItemId(lineItemId);
        costingSpillageUsage.setCostingSpillageId(costingSpillage.getId());
        costingSpillageUsageRepository.save(costingSpillageUsage);
    }

    public List<Costing> findCostingOnGrouping(Long costingId) {
        return costingRepository.findAllById(
                costingGroupRepository.getCostingGroupsByParentCostingId(costingId)
                        .stream().map(CostingGroup::getChildCostingId).collect(Collectors.toList())
        );
    }

    public Map<Long, BigDecimal> getGroupingsQuantity(Long costingId) {
        return costingGroupRepository.getCostingGroupsByParentCostingId(costingId).stream()
                .collect(Collectors.toMap(CostingGroup::getChildCostingId, CostingGroup::getQuantity)
                );
    }

    public Map<Long, String>  getCategories() {
        return lookupCostingCategoryRepository.findByMemberIdInOrderByCategory(List.of(AutorisationUtils.getCurrentUserMid(), -1L)).stream().collect(Collectors.toMap(LookupCostingCategory::getId, LookupCostingCategory::getCategory));
    }
}