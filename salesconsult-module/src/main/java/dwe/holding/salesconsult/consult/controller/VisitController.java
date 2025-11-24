package dwe.holding.salesconsult.consult.controller;

import dwe.holding.admin.authorisation.member.LocalMemberRepository;
import dwe.holding.admin.authorisation.member.MemberRepository;
import dwe.holding.admin.expose.UserService;
import dwe.holding.admin.model.LocalMember;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.customer.lookup.repository.RoomLookupRepository;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.repository.VisitRepository;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.shared.model.type.YesNoEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

@AllArgsConstructor
@Controller
@RequestMapping(path = "/consult")
@Slf4j
public class VisitController {

    private final VisitRepository visitRepository;
    private final CustomerService customerService;
    private final MemberRepository memberRepository;
    private final RoomLookupRepository roomLookupRepository;
    private final UserService userService;

    @GetMapping("/customer/{customerId}/visits")
    String getVisitsForCustomer(@PathVariable Long customerId, Model model) {
        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        model.addAttribute("customerId", customer.id())
                .addAttribute("visits",
                        visitRepository.findByMemberIdAndPet_IdInOrderByAppointment_VisitDateTimeDesc(77L, customer.pets().stream().map(CustomerService.Pet::id).toList()))
                .addAttribute("memberLocals",
                        memberRepository.findByShortCode("GWZ").getLocalMembers().stream().collect(Collectors.toMap(LocalMember::getId, LocalMember::getLocalMemberName)));
        return "consult-module/visit/list";
    }


    @GetMapping("/customer/{customerId}/visit/{visitId}")
    String editVisit(@PathVariable Long customerId, @PathVariable Long visitId, Model model) {
        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        Visit visit = visitRepository.findByMemberIdAndId(77L, visitId);
        model.addAttribute("customer", customer)
                .addAttribute("selectedPet", customer.pets().get(0))
                .addAttribute("visit", visit)
                .addAttribute("appointment", visit.getAppointment())
                .addAttribute("petsOnVisit", visit.getAppointment().getVisits().stream().map(Visit::getPet).map(pet -> new PresentationElement(pet.getId(), pet.getNameWithDeceased()))
                        .sorted(Comparator.comparing(PresentationElement::getId)).toList())
                .addAttribute("allLineItems", visit.getAppointment().getLineItems(visit.getPet().getId()))

                .addAttribute("ynvaluesList", YesNoEnum.getWebList())
                .addAttribute("rooms", roomLookupRepository.getByMemberIdOrderByRoom(77L).stream().map(rec -> new DoubleText(rec.getRoom(), rec.getRoom())).toList()) // TODO: AutorisationUtils.getCurrentUserMid());
                .addAttribute("staffList", userService.getStaffMembers(77L).stream().map(rec -> new DoubleText(rec.getName(), rec.getName())).toList()) // TODO: AutorisationUtils.getCurrentUserMid());
                .addAttribute("reasons", new ArrayList())
                .addAttribute("templates", memberRepository.findByShortCode("GWZ").getLocalMembers().iterator().next());

        return "consult-module/visit/action";
    }

    public record DoubleText(String id, String name) {
    }
}