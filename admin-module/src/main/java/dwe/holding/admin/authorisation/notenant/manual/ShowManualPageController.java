package dwe.holding.admin.authorisation.notenant.manual;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@AllArgsConstructor
@RequestMapping("/app/generic")
public class ShowManualPageController {
    private final ManualRepository manualRepository;

    @RequestMapping("/manual/{id}")
    String showManualPage(@PathVariable Long id, Model model) {
        model.addAttribute("manual", manualRepository.findById(id).orElseThrow());
        return "admin-module/manual/action";
    }

}
