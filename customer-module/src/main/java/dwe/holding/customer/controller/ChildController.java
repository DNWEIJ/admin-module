package dwe.holding.customer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/customer")
@Slf4j
public class ChildController {

    private final MessageSource messageSource;

    public ChildController(ResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping("/customer/{id}/child")
    String show() {
        return "customer-module/child/action";
    }
}