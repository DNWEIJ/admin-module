package dwe.holding.supplyinventory.controller;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.model.Distributor;
import dwe.holding.supplyinventory.model.Supply;
import dwe.holding.supplyinventory.repository.DistributorRepository;
import dwe.holding.supplyinventory.repository.LookupProductCategoryRepository;
import dwe.holding.supplyinventory.repository.SuppliesRepository;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@RequestMapping("/supplies/distributor")
@AllArgsConstructor
public class DistributorSuppliesController {
    private final SuppliesRepository suppliesRepository;
    private final DistributorRepository distributorRepository;
    private final LookupProductCategoryRepository lookupProductCategoryRepository;

    @GetMapping("/supplies")
    String listScreen(Model model, @RequestParam(required = false) Boolean dialog) {
        dialog = dialog != null && dialog;

        setModelData(model);
        model
                .addAttribute("salesType", new ProductController.SalesTypeDummy())
                .addAttribute("costingSearchUrl", "/supplies/distributor/")
                .addAttribute("costingSearchForm", new ProductController.ListForm(null, null, Boolean.TRUE))
                .addAttribute("supplies", List.of())
        ;

        return dialog ? "supplies-module/distributor/supplies/htmx/supplydialog" : "supplies-module/distributor/supplies/list";
    }

    @PostMapping("/supplies")
    String userSelectedGetSuppliesHtmx(Model model, String distributorName) {
        model
                .addAttribute("supplies", suppliesRepository.findByDistributorNameIgnoreCase(distributorName))
                .addAttribute("isFromHere", false)
        ;
        return "supplies-module/distributor/supplies/htmx/suppliesbody";
    }


    @GetMapping("/supply/{supplyId}/partialedit")
    String readCostingLineHtmx(Model model, @PathVariable Long supplyId) {
        model
                .addAttribute("supply", suppliesRepository.findById(supplyId).orElseThrow())
                .addAttribute("categories", lookupProductCategoryRepository.findByDeletedOrderByCategoryName(YesNoEnum.No))
        ;
        return "supplies-module/distributor/supplies/htmx/suppliesbody::editableTR";
    }

    @GetMapping("/supply/{supplyId}/partialcancel")
    String cancelSupplyLineHtmx(Model model, @PathVariable Long supplyId) {
        model
                .addAttribute("supply", suppliesRepository.findById(supplyId).orElseThrow())
                .addAttribute("isFromHere", true)
        ;
        return "supplies-module/distributor/supplies/htmx/suppliesbody::readonlyTR";
    }

    @PostMapping("/supply/{supplyId}/partialsave")
    String updateCostingLineHtmx(Supply supplyForm, Model model, @PathVariable Long supplyId) {
        Supply supply = suppliesRepository.findById(supplyId).orElseThrow();
        supply.setNomenclature(supplyForm.getNomenclature());
        supply.setQuantityPerPackage(supplyForm.getQuantityPerPackage());
        supply.setMinQuantity(supplyForm.getMinQuantity());
        supply.setBuyQuantity(supplyForm.getBuyQuantity());
        supply.setPrice(supplyForm.getPrice());
        supply.setItemNumber((supplyForm.getItemNumber()));
        supply.setBarcode(supplyForm.getBarcode());
        Supply savedSupply = suppliesRepository.save(supply);
        return cancelSupplyLineHtmx(model, savedSupply.getId());
    }


    @GetMapping("/supply")
    String SuppliesScreen(Model model) {
        model.addAttribute("action", "Create");
        model.addAttribute("supply", newSupplies())
                .addAttribute("distributorList", distributorRepository.findByMemberId(AutorisationUtils.getCurrentMember().getId()).stream()
                        .map(distributor -> new PresentationElement(distributor.id(), distributor.distributorName(), true)).toList())
                ;
        return "supplies-module/distributor/supplies/action";
    }

    @GetMapping("/supply/{supplyId}")
    String showEditScreen(@PathVariable @NotNull Long supplyId, Model model) {
        // if we goging to change, the use distributor id
        model.addAttribute("action", "Edit");
        Supply supply = suppliesRepository.findById(supplyId).orElseThrow();
        if (supply.getDistributor() == null) supply.setDistributor(new Distributor());
        model
                .addAttribute("supply", supply)
                .addAttribute("distributorList", distributorRepository.findByMemberId(AutorisationUtils.getCurrentMember().getId()).stream()
                        .map(distributor -> new PresentationElement(distributor.id(), distributor.distributorName(), true)).toList())
        ;
        return "supplies-module/distributor/supplies/action";
    }


    @PostMapping("/supply")
    String SuppliesScreen(Supply supplyForm, RedirectAttributes redirect) {
        Supply supply = new Supply();
        if (supplyForm.getId() != null)
            supply = suppliesRepository.findById(supplyForm.getId()).orElseThrow();

        supply.setNomenclature(supplyForm.getNomenclature());
        supply.setQuantityPerPackage(supplyForm.getQuantityPerPackage());
        supply.setMinQuantity(supplyForm.getMinQuantity());
        supply.setBuyQuantity(supplyForm.getBuyQuantity());
        supply.setPrice(supplyForm.getPrice());
        supply.setItemNumber((supplyForm.getItemNumber()));
        supply.setBarcode(supplyForm.getBarcode());
        supply.setDescriptionOfDistributor(supplyForm.getDescriptionOfDistributor());
        supply.setDistributor(distributorRepository.getReferenceById(supplyForm.getDistributor().getId()));
        supply.setDistributorName("");
        suppliesRepository.save(supply);
        redirect.addFlashAttribute("message", "label.saved");
        return "redirect:/supplies/distributor/supplies";
    }


    private void setModelData(Model model) {
// TODO we need to shift to the distributor link, not just have the name to loos.... think about how nt to tight to tight...
//        model.addAttribute("distributorList", distributorRepository.findByMemberId(AutorisationUtils.getCurrentMember().getId()).stream()
//                .map(distributor -> new PresentationElement(distributor.id(), distributor.distributorName(), true)).toList()
//        );
//
        model.addAttribute("distributorList", suppliesRepository.findByMemberIdGroupByDistributorName(AutorisationUtils.getCurrentMember().getId()).stream()
                .map(distributor -> new PresentationElement(distributor, distributor, true)).toList()
        );
    }

    private Supply newSupplies() {
        return Supply.builder().distributor(Distributor.builder().id(null).build()).build();
    }
}