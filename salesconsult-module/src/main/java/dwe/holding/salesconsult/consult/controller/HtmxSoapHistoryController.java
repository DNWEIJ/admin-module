package dwe.holding.salesconsult.consult.controller;

import dwe.holding.admin.model.tenant.LocalMember;
import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.customer.client.repository.PetRepository;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.salesconsult.consult.SoapService;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@AllArgsConstructor
@Controller
@RequestMapping(path = "/consult")
@Slf4j
public class HtmxSoapHistoryController {
    private final SoapService soapService;
    private final PetRepository petRepository;
    private final CustomerService customerService;

    @GetMapping("/visit/customer/{customerId}/pet/{petId}/history")
    String returnHistoryModal(@PathVariable Long customerId, @PathVariable Long petId, Model model) {
        return "";
    }

    @GetMapping("/visit/customer/{customerId}/pet/{petId}/soap")
    String returnSoapModal(@PathVariable Long customerId, @PathVariable Long petId, Model model) {
        // get all details for petId
        // validate customer and pet
        customerService.searchCustomerAndPet(customerId, petId);
        model.addAttribute("appointments", soapService.getSoap(petId));
        model.addAttribute("memberLocals", AutorisationUtils.getLocalMemberMap());
        model.addAttribute("petId", petId);
        model.addAttribute("petId", petId);
        model.addAttribute("clinic_name", AutorisationUtils.getCurrentLocalMember().getLocalMemberName());
        model.addAttribute("clinic_address", editAddress(AutorisationUtils.getCurrentLocalMember()));

        model.addAttribute("pet", petRepository.findByIdAndMemberId(petId, AutorisationUtils.getCurrentUserMid()));
        return "consult-module/soap/soap";
    }

    @GetMapping("/visit/customer/{customerId}/pet/{petId}/soap/print")
    @PostMapping("/visit/customer/{customerId}/pet/{petId}/soap")
    String printSoapModal(@PathVariable Long customerId, @PathVariable Long petId, String extraComment, Model model) {
        returnSoapModal(customerId,petId,model);
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