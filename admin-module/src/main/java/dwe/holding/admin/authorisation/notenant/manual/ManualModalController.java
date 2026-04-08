package dwe.holding.admin.authorisation.notenant.manual;

import dwe.holding.admin.model.notenant.Manual;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class ManualModalController {

    private final ManualRepository manualRepository;

    @GetMapping("/info/**")
    public String handle(HttpServletRequest request, Model model) {
        String path = request.getRequestURI()
                .substring(request.getContextPath().length() + "/info".length());
        path = path.startsWith("/") ? path.substring(1) : path;
        path = path.replace("/","_");
         model.addAttribute("manual",manualRepository.findByName(path).orElse(Manual.builder().build()));
        return "admin-module/manual/dialog";

    }

}
