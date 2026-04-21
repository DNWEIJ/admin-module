package dwe.holding.vmas.chart;

import org.springframework.web.bind.annotation.GetMapping;

// @Controller
public class ChartViewController {

    @GetMapping("/charts")
    public String chartsIndex() {
        return "chart-module/index";
    }

    @GetMapping("/charts/estimates-timeline")
    public String estimatesTimeline() {
        return "chart-module/estimates-timeline";
    }

    @GetMapping("/charts/revenue-analysis")
    public String revenueAnalysis() {
        return "chart-module/revenue-analysis";
    }

    @GetMapping("/charts/category-breakdown")
    public String categoryBreakdown() {
        return "chart-module/category-breakdown";
    }

    @GetMapping("/charts/distribution")
    public String distribution() {
        return "chart-module/distribution";
    }

    @GetMapping("/charts/dashboard")
    public String dashboard() {
        return "chart-module/dashboard";
    }
}
