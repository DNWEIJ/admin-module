package dwe.holding.salesconsult.consult.controller;

import dwe.holding.admin.authorisation.member.MemberRepository;
import dwe.holding.admin.expose.UserService;
import dwe.holding.admin.model.LocalMember;
import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.customer.client.controller.CustomerController;
import dwe.holding.customer.client.model.Customer;
import dwe.holding.customer.client.model.type.CustomerStatusEnum;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.customer.lookup.repository.RoomLookupRepository;
import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.repository.VisitRepository;
import dwe.holding.salesconsult.sales.Service.LineItemService;
import dwe.holding.salesconsult.sales.controller.PetsForm;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@AllArgsConstructor
@Controller
@RequestMapping(path = "/consult")
@Slf4j
public class VisitController {

    private final MemberRepository memberRepository;
    private final VisitRepository visitRepository;

    private final RoomLookupRepository roomLookupRepository;
    private final CustomerService customerService;
    private final UserService userService;
    private final LineItemService lineItemService;

    @GetMapping("/visit/search")
    String firstStepStart(Model model) {
        model.addAttribute("form", new CustomerController.CustomerForm(true, false, false, false))
                .addAttribute("customer", Customer.builder().newsletter(YesNoEnum.No).status(CustomerStatusEnum.NORMAL).build());

        return "/consult-module/visit/searchCustomer";
    }

    @GetMapping("/visit/search/{customerId}")
    String secondStepCustomerFoundShowPetsAndDateTimeAndLoc(@PathVariable @NotNull Long customerId, Model model, RedirectAttributes redirect) {

        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        model.addAttribute("customer", customer)
                .addAttribute("pets", customer.pets().stream().filter(pet -> !pet.deceased()).toList())
                .addAttribute("deceasedPets", customer.pets().stream().filter(CustomerService.Pet::deceased).toList())
                .addAttribute("form", new CustomerController.CustomerForm(true, false, false, false))
                .addAttribute("reasons", customerService.getReasons().stream()
                        .map(rec -> new PresentationElement(rec.getId(), rec.getDefinedPurpose(), rec.getTimeInMinutes().toString())).toList())
                .addAttribute("appointment", Appointment.builder().visitDateTime(LocalDateTime.now()).localMemberId(AutorisationUtils.getCurrentUserMlid()).build())
                .addAttribute("localMembersList", AutorisationUtils.getLocalMemberList())
                .addAttribute("rooms", roomLookupRepository.findByLocalMemberIdAndMemberId(AutorisationUtils.getCurrentUserMlid(), AutorisationUtils.getCurrentUserMid())
                        .stream().map(rec -> new PresentationElement(rec.getId(), rec.getRoom(), true)).toList())
                .addAttribute("staffList", userService.getStaffMembers(AutorisationUtils.getCurrentUserMid()))
                .addAttribute("timeList", IntStream.rangeClosed(1, 24).map(i -> i * 5).mapToObj(i -> new PresentationElement((long) i, String.valueOf(i))).toList()
                )
        ;
        return "consult-module/visit/petanddateselectpage";
    }

    @PostMapping("/visit/search/{customerId}")
    String thirdStepPetFoundCreateAppointmentVisit(@PathVariable @NotNull Long customerId, @ModelAttribute @NotNull PetsForm petsForm, Model model, RedirectAttributes redirect) {
        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        List<PetsForm.FormPet> pets = petsForm.getFormPet().stream().filter(pet -> pet.getChecked() != null).toList();
        if (customer == null || pets.isEmpty()) {
            model.addAttribute("message", "Something went wrong. Please try again");
            return "redirect:/visit/search/" + customerId;
        }
        return "";
    }


    // List visits from customer/customer
    @GetMapping("/customer/{customerId}/visits")
    String getVisitsForCustomer(@PathVariable Long customerId, Model model) {
        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        model.addAttribute("customerId", customer.id())
                .addAttribute("visits",
                        visitRepository.findByMemberIdAndPet_IdInOrderByAppointment_VisitDateTimeDesc(AutorisationUtils.getCurrentUserMid(), customer.pets().stream().map(CustomerService.Pet::id).toList()))
                .addAttribute("memberLocals",
                        memberRepository.findByShortCode("GWZ").getLocalMembers().stream().collect(Collectors.toMap(LocalMember::getId, LocalMember::getLocalMemberName)));
        return "consult-module/visit/list";
    }

    @GetMapping("/customer/{customerId}/visit/{visitId}")
    String editVisit(@PathVariable Long customerId, @PathVariable Long visitId, Model model) {
        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId);
        model.addAttribute("customer", customer)
                .addAttribute("selectedPet", customer.pets().get(0))
                .addAttribute("visit", visit)
                .addAttribute("appointment", visit.getAppointment())
                .addAttribute("petsOnVisit", visit.getAppointment().getVisits().stream().map(Visit::getPet).map(pet -> new PresentationElement(pet.getId(), pet.getNameWithDeceased()))
                        .sorted(Comparator.comparing(PresentationElement::getId)).toList())
                .addAttribute("allLineItems", lineItemService.getLineItemsForPet(visit.getPet().getId(), visit.getAppointment().getId()))

                .addAttribute("ynvaluesList", YesNoEnum.getWebList())
                .addAttribute("rooms", customerService.getRoomList())
                .addAttribute("staffList", userService.getStaffMembers(AutorisationUtils.getCurrentUserMid()))
                .addAttribute("reasons", new ArrayList())  // TODO
                .addAttribute("templates", new ArrayList()); // TODO
        return "consult-module/visit/action";
    }
}