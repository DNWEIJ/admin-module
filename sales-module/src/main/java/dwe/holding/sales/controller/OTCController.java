package dwe.holding.sales.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/sales")
@Controller
public class OTCController {

    @GetMapping("/otc/search")
    void firstSearchCustomer() {

    }
}