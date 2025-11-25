package dwe.holding.vmas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("vmas")
@Controller
public class InitialController {

    @GetMapping("/index")
    String index() {
        return "vmas-module/index";
    }
}