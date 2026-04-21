package dwe.holding.vmas.chart;
import dwe.holding.salesconsult.consult.model.Estimate;
import dwe.holding.salesconsult.consult.model.EstimateForPet;
import dwe.holding.salesconsult.consult.model.EstimateLineItem;
import dwe.holding.salesconsult.consult.repository.EstimateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

// @RestController
@RequestMapping("/api/charts")
public class ChartDataController {
    
  //  @Autowired
    private EstimateRepository estimateRepository;
    public enum GroupingPeriod {
        DAY,
        WEEK,
        MONTH,
        YEAR
    }
    @GetMapping("/estimates-by-date")
    public Map<String, Object> getEstimatesByDate(@RequestParam GroupingPeriod period) {

       //  if(period == null) period = GroupingPeriod.MONTH;

        List<Estimate> estimates = estimateRepository.findAll();

        Map<String, Long> countByPeriod = estimates.stream()
                .collect(Collectors.groupingBy(
                        e -> {
                            LocalDate date = e.getEstimateDate();
                            switch (period) {
                                case DAY:
                                    return date.toString();
                                case WEEK:
                                    WeekFields wf = WeekFields.ISO;
                                    int week = date.get(wf.weekOfWeekBasedYear());
                                    int year = date.get(wf.weekBasedYear());
                                    return year + "-W" + week;
                                case MONTH:
                                    return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
                                case YEAR:
                                    return String.valueOf(date.getYear());
                                default:
                                    throw new IllegalArgumentException();
                            }
                        },
                        TreeMap::new,
                        Collectors.counting()
                ));

        List<String> dates = new ArrayList<>(countByPeriod.keySet());
        List<Long> counts = new ArrayList<>(countByPeriod.values());

        return Map.of("dates", dates, "counts", counts);
    }
    
    @GetMapping("/revenue-by-date")
    public Map<String, Object> getRevenueByDate() {
        List<Estimate> estimates = estimateRepository.findAll();
        
        Map<LocalDate, BigDecimal> revenueByDate = estimates.stream()
            .collect(Collectors.groupingBy(
                e -> e.getEstimateDate(),
                Collectors.reducing(
                    BigDecimal.ZERO,
                    e -> e.getEstimateLineItems().stream()
                        .map(EstimateLineItem::getTotalIncTax)
                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                    BigDecimal::add
                )
            ));
        
        List<String> dates = revenueByDate.keySet().stream()
            .sorted()
            .map(LocalDate::toString)
            .toList();
        
        List<BigDecimal> revenues = revenueByDate.keySet().stream()
            .sorted()
            .map(revenueByDate::get)
            .toList();
        
        return Map.of("dates", dates, "revenues", revenues);
    }
    
    @GetMapping("/top-categories")
    public Map<String, Object> getTopCategories() {
        List<Estimate> estimates = estimateRepository.findAll();
        
        Map<Long, BigDecimal> categoryRevenue = estimates.stream()
            .flatMap(e -> e.getEstimateLineItems().stream())
            .collect(Collectors.groupingBy(
                EstimateLineItem::getCategoryId,
                Collectors.reducing(
                    BigDecimal.ZERO,
                    EstimateLineItem::getTotalIncTax,
                    BigDecimal::add
                )
            ));
        
        List<Map.Entry<Long, BigDecimal>> sorted = categoryRevenue.entrySet().stream()
            .sorted(Map.Entry.<Long, BigDecimal>comparingByValue().reversed())
            .limit(10)
            .toList();
        
        List<String> categories = sorted.stream()
            .map(e -> "Category " + e.getKey())
            .toList();
        
        List<BigDecimal> revenues = sorted.stream()
            .map(Map.Entry::getValue)
            .toList();
        
        return Map.of("categories", categories, "revenues", revenues);
    }
    
    @GetMapping("/items-per-estimate")
    public Map<String, Object> getItemsPerEstimate() {
        List<Estimate> estimates = estimateRepository.findAll();
        
        Map<Long, Long> distribution = estimates.stream()
            .collect(Collectors.groupingBy(
                e -> (long) e.getEstimateLineItems().size(),
                Collectors.counting()
            ));
        
        List<Long> itemCounts = distribution.keySet().stream().sorted().toList();
        List<Long> estimateCounts = itemCounts.stream().map(distribution::get).toList();
        
        return Map.of("itemCounts", itemCounts, "estimateCounts", estimateCounts);
    }
    
    @GetMapping("/tax-breakdown")
    public Map<String, Object> getTaxBreakdown() {
        List<Estimate> estimates = estimateRepository.findAll();
        
        BigDecimal totalCost = estimates.stream()
            .flatMap(e -> e.getEstimateLineItems().stream())
            .map(EstimateLineItem::getSalesPriceExTax)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalTax = estimates.stream()
            .flatMap(e -> e.getEstimateLineItems().stream())
            .map(EstimateLineItem::getTaxPortionOfProduct)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalProcessingFee = estimates.stream()
            .flatMap(e -> e.getEstimateLineItems().stream())
            .map(EstimateLineItem::getProcessingFeeExTax)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return Map.of(
            "labels", List.of("Cost", "Tax", "Processing Fee"),
            "values", List.of(totalCost, totalTax, totalProcessingFee)
        );
    }
}
