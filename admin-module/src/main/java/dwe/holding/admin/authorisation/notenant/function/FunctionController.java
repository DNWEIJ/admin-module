package dwe.holding.admin.authorisation.notenant.function;

import dwe.holding.admin.model.notenant.Function;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Validated
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequestMapping("/admin")
public class FunctionController {
    private final FunctionRepository functionRepository;

    public FunctionController(FunctionRepository functionRepository) {
        this.functionRepository = functionRepository;
    }

    @PostMapping("/function")
    @CacheEvict(value = "functions", allEntries = true)
    public String save(@Valid Function function, BindingResult bindingResult, Model model, RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "admin-module/function/action";
        }
        function.setId(null);
        function.setVersion(null);
        functionRepository.save(function);

        redirect.addFlashAttribute("message", "label.saved");
        return "redirect:/admin/functions";
    }

    @GetMapping("/function")
    String newScreen(Model model) {
        model.addAttribute("action", "Create");
        model.addAttribute("function", new Function());
        return "admin-module/function/action";

    }

    @GetMapping("/function/{id}")
    String showEditScreen(@PathVariable @NotNull Long id, Model model) {
        model.addAttribute("action", "Edit");
        model.addAttribute("function", functionRepository.findById(id).orElseThrow());
        return "admin-module/function/action";
    }

    @GetMapping("/functions")
    String listScreen(Model model) {
        model.addAttribute("action", "List");
        model.addAttribute("functions", functionRepository.findAll());
        return "admin-module/function/list";
    }
}