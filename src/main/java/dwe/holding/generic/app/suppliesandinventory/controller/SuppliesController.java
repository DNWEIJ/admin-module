package dwe.holding.generic.app.suppliesandinventory.controller;

import dwe.holding.generic.admin.model.PresentationFunction;
import dwe.holding.generic.admin.security.AutorisationUtils;
import dwe.holding.generic.app.suppliesandinventory.model.Distributor;
import dwe.holding.generic.app.suppliesandinventory.model.Supplies;
import dwe.holding.generic.app.suppliesandinventory.repository.DistributorRepository;
import dwe.holding.generic.app.suppliesandinventory.repository.SuppliesRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Controller
public class SuppliesController {
    private final SuppliesRepository suppliesRepository;
    private final DistributorRepository distributorRepository;

    public SuppliesController(SuppliesRepository suppliesRepository, DistributorRepository distributorRepository) {
        this.suppliesRepository = suppliesRepository;
        this.distributorRepository = distributorRepository;
    }

    @GetMapping("/supplies/supplies")
    String SuppliesScreen(Model model) {
        model.addAttribute("action", "Create");
        model.addAttribute("supply", newSupplies());
        setModelData(model);
        return "supplies-module/supplies/action";
    }

    @GetMapping("/supplies/supplies/{id}")
    String showEditScreen(@PathVariable @NotNull UUID id, Model model) {
        model.addAttribute("action", "Edit");
        Supplies supply = suppliesRepository.findById(id).orElseThrow();
        if (supply.getDistributor() == null) supply.setDistributor(new Distributor());
        model.addAttribute("supply", supply);
        setModelData(model);
        return "supplies-module/supplies/action";
    }

    @GetMapping("/supplies/supplies/list")
    String listScreen(Model model) {
        model.addAttribute("action", "List");
        model.addAttribute("supplies", suppliesRepository.findAll());
        return "supplies-module/supplies/list";
    }

    private void setModelData(Model model) {
        model.addAttribute("distributorList", distributorRepository.findByMemberId(AutorisationUtils.getCurrentMember().getId()).stream()
                .map(distributor -> new PresentationFunction(distributor.getId(), distributor.getDistributorName(), true)).toList()
        );
    }

    private Supplies newSupplies() {
        return Supplies.builder().distributor(Distributor.builder().id(null).build()).build();
    }
}