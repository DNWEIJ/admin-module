package dwe.holding.salesconsult.consult.controller;

import dwe.holding.admin.model.tenant.LocalMember;
import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.repository.PetRepository;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.salesconsult.consult.SoapAndHistoryService;
import dwe.holding.supplyinventory.expose.CostingService;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Locale;

@AllArgsConstructor
@Controller
@RequestMapping(path = "/consult")
@Slf4j
public class HtmxSoapAndHistoryController {
    private final SoapAndHistoryService soapAndHistoryService;
    private final PetRepository petRepository;
    private final CustomerService customerService;
    private final CostingService costingService;
    private final MessageSource messageSource;

    @GetMapping("/visit/customer/{customerId}/pet/{petId}/history/{action}")
    String returnHistoryModal(@PathVariable Long customerId, @PathVariable Long petId, @PathVariable String action, Model model, Locale local) {
        CustomerService.Customer customer = customerService.searchCustomerAndPet(customerId, petId);

        switch (action) {
            case "weight": {
                model
                        .addAttribute("headertitle", messageSource.getMessage("label.history.weight", null, local))
                        .addAttribute("historyItems", soapAndHistoryService.getHistoryForTempWeightClugose(petId))
                        .addAttribute("fragment", "consult-module/visit/dialog/history/tempweightglucosehistory");
                break;
            }
            case "product": {
                model
                        .addAttribute("headertitle", messageSource.getMessage("label.history.product", null, local))
                        .addAttribute("historyItems", soapAndHistoryService.getHistoryForProducts(petId))
                        .addAttribute("categoryNames", costingService.getCategories())
                        .addAttribute("fragment", "consult-module/visit/dialog/history/productshistory");
                break;
            }
            case "diagnose": {
                model
                        .addAttribute("headertitle", messageSource.getMessage("label.history.diagnose", null, local))
                        .addAttribute("historyItems", soapAndHistoryService.getHistoryForDiagnose(petId))
                        .addAttribute("fragment", "consult-module/visit/dialog/history/diagnosehistory");
                break;
            }
            default: {
                model.addAttribute("fragment", "");
            }
        }
        return "consult-module/visit/dialog/historydialog";
    }

    @GetMapping("/visit/customer/{customerId}/pet/{petId}/soap")
    String returnSoapModal(@PathVariable Long customerId, @PathVariable Long petId, Model model) {
        // get all details for petId
        // validate customer and pet
        customerService.searchCustomerAndPet(customerId, petId);
        model.addAttribute("appointments", soapAndHistoryService.getSoap(petId));
        model.addAttribute("memberLocals", AutorisationUtils.getLocalMemberMap());
        model.addAttribute("petId", petId);
        model.addAttribute("petId", petId);
        model.addAttribute("clinic_name", AutorisationUtils.getCurrentLocalMember().getLocalMemberName());
        model.addAttribute("clinic_address", editAddress(AutorisationUtils.getCurrentLocalMember()));

        model.addAttribute("pet", petRepository.findByIdAndMemberId(petId, AutorisationUtils.getCurrentUserMid()));
        return "consult-module/soap/soap";
    }

    @PostMapping("/visit/customer/{customerId}/pet/{petId}/soap")
    String printSoapModal(@PathVariable Long customerId, @PathVariable Long petId, String extraComment, Model model) {
        returnSoapModal(customerId, petId, model);
        model.addAttribute("extraComment", extraComment);
        return "consult-module/soap/soapprint";
    }


    private String editAddress(LocalMember memberLocal) {
        StringBuffer sb = new StringBuffer();
        sb.append((memberLocal.getAddress1() == null ? "" : memberLocal.getAddress1()));
        if (StringUtils.isNotEmpty(memberLocal.getAddress1())) {
            sb.append(" - ");
        }
        sb.append((memberLocal.getAddress2() == null ? "" : memberLocal.getAddress2()));
        if (StringUtils.isNotEmpty(memberLocal.getAddress2()) && StringUtils.isNotEmpty(memberLocal.getAddress3())) {
            sb.append(" - ");
        }
        sb.append((memberLocal.getAddress3() == null ? "" : memberLocal.getAddress3()));
        return sb.toString();
    }
}