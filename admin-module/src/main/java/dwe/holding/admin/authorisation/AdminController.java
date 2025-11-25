package dwe.holding.admin.authorisation;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/admin/index")
    String indexAdminScreen(Model model) {
        model.addAttribute("applicationName", "admin-module");
        return "/admin-module/index";
    }
}