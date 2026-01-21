package dwe.holding.salesconsult.consult.controller;

import dwe.holding.admin.expose.UserService;
import dwe.holding.admin.preferences.LocalMemberPreferences;
import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.customer.client.controller.form.CustomerForm;
import dwe.holding.customer.client.model.Customer;
import dwe.holding.customer.client.model.type.CustomerStatusEnum;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.salesconsult.consult.model.AnalyseItem;
import dwe.holding.salesconsult.consult.model.Appointment;
import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.consult.repository.*;
import dwe.holding.salesconsult.consult.service.AppointmentVisitService;
import dwe.holding.salesconsult.sales.Service.LineItemService;
import dwe.holding.salesconsult.sales.controller.SalesType;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.shared.model.type.YesNoEnum;
import dwe.holding.supplyinventory.expose.CostingService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static dwe.holding.salesconsult.sales.controller.ModelHelper.*;

@AllArgsConstructor
@Controller
@RequestMapping(path = "/consult")
@Slf4j
public class VisitController {
    private final VisitRepository visitRepository;
    private final LookupRoomRepository lookupRoomRepository;
    private final LookupPurposeRepository lookupPurposeRepository;
    private final AnalyseDescriptionRepository analyseDescriptionRepository;
    private final AnalyseItemRepository analyseItemRepository;
    private final LookupDiagnosesRepository lookupDiagnosesRepository;
    private final LookupLocationRepository lookupLocationRepository;
    private final DiagnoseRepository diagnoseRepository;
    private final UserService userService;
    private final CustomerService customerService;
    private final LineItemService lineItemService;
    private final AppointmentVisitService appointmentVisitService;
    private final CostingService costingService;
    private final ObjectMapper objectMapper;
    private final CustomerForm customerForm;

    @GetMapping("/visit/search")
    String firstStepCreateVisitFindCustomer(Model model) {
        model.addAttribute("form", customerForm)
                .addAttribute("customer", Customer.builder().newsletter(YesNoEnum.No).status(CustomerStatusEnum.NORMAL).build())
                .addAttribute("textLabel", "label.title.visit")
                .addAttribute("url", "/consult/visit/search/");
        return "/salesconsult-generic-module/customersearchpage";
    }

    @GetMapping("/visit/search/{customerId}")
    String secondStepCustomerFoundShowPetsAndDateTimeAndLoc(@PathVariable @NotNull Long customerId, Model model) {

        updateCustomerAndPetsInModel(model, customerService.searchCustomer(customerId));
        updateRoomsInModel(model, lookupRoomRepository);
        updateLocationsInModel(model, lookupLocationRepository);
        updateDiagnosesInModel(model, lookupDiagnosesRepository, List.of(-1L));
        updateReasonsInModel(model, lookupPurposeRepository);
        model
                .addAttribute("form", customerForm)
                .addAttribute("appointment", Appointment.builder().visitDateTime(LocalDateTime.now()).localMemberId(AutorisationUtils.getCurrentUserMlid()).build())
                .addAttribute("localMembersList", AutorisationUtils.getLocalMemberList())
                .addAttribute("staffList", userService.getStaffMembers(AutorisationUtils.getCurrentUserMid()))
                .addAttribute("timeList", IntStream.rangeClosed(1, 24).map(i -> i * 5).mapToObj(i -> new PresentationElement((long) i, String.valueOf(i))).toList())
        ;
        return "consult-module/visit/petanddateselectpage";
    }

    @PostMapping("/visit/customer/{customerId}")
    String thirdStepPetFoundCreateAppointmentAndVisit(@NotNull @PathVariable Long customerId, @NotNull CreateVisitForm createVisitForm, Model model) {

        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        List<AppointmentVisitService.CreatePet> pets = createVisitForm.formPet().stream().filter(pet -> pet.checked() != null && pet.checked()).toList();
        if (customer == null || pets.isEmpty()) {
            model.addAttribute("message", "Something went wrong. Please try again");
            return "redirect:/visit/search/" + customerId;
        }
        Appointment app = appointmentVisitService.createAppointmentVisit(pets, customerId, SalesType.VISIT);
        Visit visit = app.getVisits().iterator().next();
        return "redirect:/consult/visit/customer/" + customerId + "/visit/" + app.getVisits().iterator().next().getId();
    }

    // List visits from customer/customer
    @GetMapping("/visit/customer/{customerId}/visits")
    String getVisitsForCustomer(@NotNull @PathVariable Long customerId, Model model) {
        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        model
                .addAttribute("customerId", customer.id())
                .addAttribute("pets", customer.pets().stream().collect(Collectors.toMap(p -> p.id(), p -> p.deceased() ? p.name() + " &dagger;" : p.name())))
                .addAttribute("visits", visitRepository.findByMemberIdAndPet_IdInOrderByAppointment_VisitDateTimeDesc(AutorisationUtils.getCurrentUserMid(), customer.pets().stream().map(CustomerService.Pet::id).toList()))
                .addAttribute("localMembersList", AutorisationUtils.getLocalMemberMap())
                .addAttribute("activeMenu", "visits");
        return "consult-module/visit/list";
    }

    @GetMapping("/visit/customer/{customerId}/visit/{visitId}")
    String editVisit(@NotNull @PathVariable Long customerId, @NotNull @PathVariable Long visitId, Model model) {
        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow();
        LocalMemberPreferences pref = objectMapper.readValue(AutorisationUtils.getCurrentLocalMember().getMetaLocalMemberPreferences().getPreferencesJson(), LocalMemberPreferences.class);

        updateLineItemsInModel(model, lineItemService.getLineItemsForPet(visit.getPet().getId(), visit.getAppointment().getId()));
        updateReasonsInModel(model, lookupPurposeRepository);
        updateRoomsInModel(model, lookupRoomRepository);
        updateLocationsInModel(model, lookupLocationRepository);
        updateDiagnosesInModel(model, lookupDiagnosesRepository, List.of(-1L));
        updatePetDiagnosesInModel(model, diagnoseRepository, AutorisationUtils.getCurrentUserMid(), visit.getPet().getId(), visit.getAppointment().getId());
        updateVisitStatusInModel(model, visit.getStatus());
        List<AnalyseItem> analyseItems = analyseItemRepository.findByMemberIdAndAppointmentIdAndPetId(AutorisationUtils.getCurrentUserMid(), visit.getAppointment().getId(), visit.getPet().getId());
        model
                .addAttribute("userIsAllowed", AutorisationUtils.getCurrentUserIsAuthorized())
                .addAttribute("customer", customer)
                .addAttribute("visit", visit)
                .addAttribute("appointment", visit.getAppointment())
                .addAttribute("petsOnAppointment", visit.getAppointment().getVisits().stream().map(Visit::getPet)
                        .map(pet -> new PresentationElement(pet.getId(), pet.getNameWithDeceased())).sorted(Comparator.comparing(PresentationElement::getId)).toList())
                // the analyse descriptions will be available when there is a description selected on the visit screen.
                .addAttribute("categoryNames", costingService.getCategories())
                .addAttribute("analyseDescription", analyseDescriptionRepository.findByMemberId(AutorisationUtils.getCurrentUserMid()))
                .addAttribute("templates", pref.getConsultTextRecords(objectMapper))
                .addAttribute("ynvaluesList", YesNoEnum.getWebList())
                .addAttribute("salesType", SalesType.VISIT)
                .addAttribute("staffList", userService.getStaffMembers(AutorisationUtils.getCurrentUserMid()))
                .addAttribute("url", "/sales/price/sell/")
                .addAttribute("analyses", analyseDescriptionRepository.findByMemberId(AutorisationUtils.getCurrentUserMid()))
                .addAttribute("analyseItems", analyseItems)
                .addAttribute("isAnalyseItemsFromDb", !analyseItems.isEmpty())
                ;


//        for the view:
//        'analyseItems' is filled initially by the saved version;
//        if no records found, then the dropdown is displayed, and the selection is made to load the analyses that, after selecting, can be saved
        return "consult-module/visit/action";
    }

    public record CreateVisitForm(List<AppointmentVisitService.CreatePet> formPet, Appointment appointment) {
    }
}