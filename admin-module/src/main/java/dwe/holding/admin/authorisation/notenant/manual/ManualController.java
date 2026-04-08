package dwe.holding.admin.authorisation.notenant.manual;

import dwe.holding.admin.model.notenant.Manual;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/admin")
public class ManualController {
    private final ManualRepository manualRepository;

    @GetMapping("/manuals")
    String showList(Model model) {
        model.addAttribute("manuals", manualRepository.findAll());
        return "admin-module/manual/list";
    }

    @GetMapping("/manual")
    String showPage(Model model) {
        model.addAttribute("manual", Manual.builder().build());
        return "admin-module/manual/action";
    }

    @GetMapping("/manual/{id}")
    String showManualPage(@PathVariable Long id, Model model) {
        model.addAttribute("manual", manualRepository.findById(id).orElseThrow());
        return "admin-module/manual/action";
    }

    @PostMapping("/manual")
    String saveManualPage(Manual manualForm) {
        if (manualForm.getId() == null) {
            manualRepository.save(
                    Manual.builder().name(manualForm.getName())
                            .htmlDescription(manualForm.getHtmlDescription())
                            .build());
        } else {
            Manual manual = manualRepository.findById(manualForm.getId()).orElseThrow();
            manual.setName(manualForm.getName());
            manual.setHtmlDescription(manualForm.getHtmlDescription());
            manualRepository.save(manual);
        }
        return "redirect:/admin/manual";

    }
}
