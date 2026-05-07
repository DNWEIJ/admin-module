package dwe.holding.supplyinventory.controller;

import dwe.holding.supplyinventory.repository.DistributorRepository;
import dwe.holding.supplyinventory.repository.LookupProductCategoryRepository;
import dwe.holding.supplyinventory.repository.SuppliesRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@AllArgsConstructor
@RequestMapping("/product")
public class ProductPurchaseController {
    private final SuppliesRepository suppliesRepository;
    private final DistributorRepository distributorRepository;
    private final LookupProductCategoryRepository lookupProductCategoryRepository;

    @AllArgsConstructor
    @Getter
    public class UrgentReorderItem {
        private Long supplyId;
        private String nomenclature;
        private BigDecimal currentStock;
        private BigDecimal minAlert;
        private BigDecimal suggestedOrderQuantity;
        private String distributorName;
        private String itemNumber;
    }

    @AllArgsConstructor
    @Getter
    public class ForecastedReorderItem {
        private Long supplyId;
        private String nomenclature;
        private BigDecimal currentStock;
        private Integer daysUntilReorder;
        private BigDecimal suggestedOrderQuantity;
        private String distributorName;
    }
    @AllArgsConstructor
    @Getter
    public class SuggestedOrderItem {
        private Long supplyId;
        private String nomenclature;
        private BigDecimal quantity;
        private String itemNumber;
        private String priority;
    }
    @AllArgsConstructor
    @Getter
    public class PendingDelivery {
        private Long orderId;
        private String supplierName;
        private LocalDate orderDate;
        private LocalDate expectedDeliveryDate;
        private BigDecimal totalItems;
        private String status;
    }
    @AllArgsConstructor
    @Getter
    public class HighConsumptionItem {
        private Long supplyId;
        private String nomenclature;
        private BigDecimal totalConsumed;
        private BigDecimal avgDaily;
    }
    @AllArgsConstructor
    @Getter
    public class SupplyOrderingDashboard {
        private String locationName = "";
        private List<UrgentReorderItem> urgentReorders;
        private List<ForecastedReorderItem> forecastedReorders;
        private Map<String, List<SuggestedOrderItem>> orderDraftsBySupplier;
        private List<PendingDelivery> pendingDeliveries;
        private List<HighConsumptionItem> highConsumptionItems;

        public SupplyOrderingDashboard() {
            this.urgentReorders = new ArrayList<>();
            this.forecastedReorders = new ArrayList<>();
            this.orderDraftsBySupplier = new HashMap<>();
            this.pendingDeliveries = new ArrayList<>();
            this.highConsumptionItems = new ArrayList<>();
        }
    }

    @GetMapping("/purchase")
    String listScreen(Model model) {
        // TODO inital screen to show what can be purchased, low on inventory

        model.addAttribute("dashboard", new SupplyOrderingDashboard());

        return "supplies-module/purchase/list";
    }
}