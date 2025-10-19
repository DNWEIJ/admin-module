package dwe.holding.generic.suppliesandinventory.controller;

import dwe.holding.generic.shared.model.type.YesNoEnum;
import dwe.holding.generic.admin.security.AutorisationUtils;
import dwe.holding.generic.suppliesandinventory.model.Distributor;
import dwe.holding.generic.suppliesandinventory.repository.DistributorRepository;
import jakarta.servlet.http.HttpServletRequest;
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



import static dwe.holding.generic.admin.security.ButtonConstants.getRedirectFor;

@Controller
@RequestMapping("/supplies")
public class DistributorController {
    private final DistributorRepository distributorRepository;

    public DistributorController(DistributorRepository distributorRepository) {
        this.distributorRepository = distributorRepository;
    }


    @PostMapping("/distributor")
    String save(@Valid Distributor distributor, BindingResult bindingResult, Model model, RedirectAttributes redirect, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "admin-module/distributor/action";
        }
        distributor.setId(null);
        distributor.setVersion(null);
        distributor.setMemberId(AutorisationUtils.getCurrentUserMid());
        Distributor savedDistributor = distributorRepository.save(distributor);

        redirect.addFlashAttribute("message", "Function saved successfully!");
        return getRedirectFor(request, savedDistributor.getId(), "redirect:/distributor");
    }

    @GetMapping("/distributor")
    String DistributorScreen(Model model) {
        model.addAttribute("action", "Create");
        setModelData(model, new Distributor());
        return "supplies-module/distributor/action";
    }


    @GetMapping("/distributor/{id}")
    String showEditScreen(@PathVariable @NotNull   Long id, Model model) {
        model.addAttribute("action", "Edit");
        setModelData(model, distributorRepository.findById(id).orElseThrow());
        return "supplies-module/distributor/action";
    }

    @GetMapping("/distributor/list")
    String listScreen(Model model) {
        model.addAttribute("action", "List");
        model.addAttribute("distributors", distributorRepository.findAll());
        return "supplies-module/distributor/list";
    }

    private void setModelData(Model model, Distributor distributor) {
        model.addAttribute("distributor", distributor);
        model.addAttribute("ynvaluesList", YesNoEnum.getWebList());
    }
}