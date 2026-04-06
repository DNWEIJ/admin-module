package dwe.holding.supplyinventory.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.model.Distributor;
import dwe.holding.supplyinventory.repository.DistributorRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/supplies")
public class DistributorController {
    private final DistributorRepository distributorRepository;

    public DistributorController(DistributorRepository distributorRepository) {
        this.distributorRepository = distributorRepository;
    }

    @GetMapping("/distributors")
    String listScreen(Model model) {
        model.addAttribute("action", "List");
        model.addAttribute("distributors", distributorRepository.findAll());
        return "supplies-module/distributor/list";
    }

    @GetMapping("/distributor")
    String DistributorScreen(Model model) {
        model.addAttribute("action", "Create");
        setModelData(model, new Distributor());
        return "supplies-module/distributor/action";
    }

    @GetMapping("/distributor/{id}")
    String showEditScreen(@PathVariable @NotNull Long id, Model model) {
        model.addAttribute("action", "Edit");
        setModelData(model, distributorRepository.findById(id).orElseThrow());
        return "supplies-module/distributor/action";
    }

    @PostMapping("/distributor")
    String save(@Valid Distributor distributor, BindingResult bindingResult, Model model, RedirectAttributes redirect) {
        // todo add duplicate check / catch duplicate
        // check on id != null
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "admin-module/distributor/action";
        }
        distributor.setId(null);
        distributor.setVersion(null);
        distributor.setMemberId(AutorisationUtils.getCurrentUserMid());
        distributorRepository.save(distributor);

        redirect.addFlashAttribute("message", "label.saved");
        return "redirect:/supplies/distributors";
    }

    private void setModelData(Model model, Distributor distributor) {
        model.addAttribute("distributor", distributor);
        model.addAttribute("ynvaluesList", YesNoEnum.getWebList());
    }
}