package dwe.holding.generic.admin.autorisation.function_role;

import dwe.holding.generic.admin.model.Function;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

import static dwe.holding.generic.admin.security.ButtonConstants.getRedirectFor;

@Controller
@Validated
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class FunctionController {
    private final FunctionRepository functionRepository;

    public FunctionController(FunctionRepository functionRepository) {
        this.functionRepository = functionRepository;
    }

    @PostMapping("/function")
    String save(@Valid Function function, BindingResult bindingResult, Model model, RedirectAttributes redirect, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "admin-module/function/action";
        }
        function.setId(null);
        function.setVersion(null);
        Function savedFunction = functionRepository.save(function);

        redirect.addFlashAttribute("message", "Function saved successfully!");
        return getRedirectFor(request, savedFunction.getId(), "redirect:/function");
    }

    @GetMapping("/function")
    String newScreen(Model model) {
        model.addAttribute("action", "Create");
        model.addAttribute("function", new Function());
        return "admin-module/function/action";

    }

    @GetMapping("/function/{id}")
    String showEditScreen(@PathVariable @NotNull UUID id, Model model) {
        model.addAttribute("action", "Edit");
        model.addAttribute("function", functionRepository.findById(id).orElseThrow());
        return "admin-module/function/action";
    }

    @GetMapping("/function/list")
    String listScreen(Model model) {
        model.addAttribute("action", "List");
        model.addAttribute("functions", functionRepository.findAll());
        return "admin-module/function/list";
    }
}