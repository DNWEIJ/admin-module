package dwe.holding.salesconsult.consult.controller;

import dwe.holding.admin.expose.UserService;
import dwe.holding.admin.model.type.AgendaTypeEnum;
import dwe.holding.admin.preferences.LocalMemberPreferences;
import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.controller.form.CustomerForm;
import dwe.holding.customer.client.model.Customer;
import dwe.holding.customer.client.model.type.CustomerStatusEnum;
import dwe.holding.customer.client.repository.CustomerRepository;
import dwe.holding.customer.client.service.CustomerFinancialInfo;
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
import dwe.holding.supplyinventory.controller.ProductController;
import dwe.holding.supplyinventory.expose.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static dwe.holding.salesconsult.sales.controller.ModelHelper.*;

@AllArgsConstructor
@Controller
@RequestMapping("/consult")
@Slf4j
public class VisitController {
    public static final String VISIT_URL = "/consult/visit/customer/{customerId}/visit/{visitId}";
    public static final String CONSULT_VISIT_SEARCH = "/consult/visit/search";

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
    private final ProductService productService;
    private final ObjectMapper objectMapper;
    private final CustomerForm customerForm;
    private final AppointmentRepository appointmentRepository;
    private final CustomerFinancialInfo customerFinancialInfo;
    private final CustomerRepository customerRepository;
    private final MessageSource messageSource;

    @GetMapping("/visit/search")
    String firstStepCreateVisitFindCustomer(Model model, HttpServletRequest request) {
        model
                .addAttribute("form", customerForm)
                .addAttribute("customer", Customer.builder().newsletter(YesNoEnum.No).status(CustomerStatusEnum.NORMAL).build())
                .addAttribute("textLabel", "label.title.visit")
                .addAttribute("customerSearchUrl", CONSULT_VISIT_SEARCH)
                .addAttribute("salesType", SalesType.VISIT);
        if (getHtmxAndAddToModel(request, model)) {
            return "/salesconsult-generic-module/customersearchpagemodal";
        } else {
            return "/salesconsult-generic-module/customersearchpage";
        }
    }

    // from the calendar this will be called with a lot preset fields, due to the context the calendar gives
    @GetMapping("/visit/search/{customerId}")
    String secondStepCustomerFoundShowPetsAndDateTimeAndLoc(@PathVariable @NotNull Long customerId,
                                                            @RequestParam(required = false) AgendaTypeEnum agendaType, @RequestParam(required = false) String resource,
                                                            @RequestParam(required = false) Long localMemberId, @RequestParam(required = false) LocalDateTime creationDate,
                                                            Model model, HttpServletRequest request) {

        updateCustomerAndPetsInModel(model, customerService.searchCustomer(customerId));
        updateRoomsInModel(model, lookupRoomRepository);
        updateLocationsInModel(model, lookupLocationRepository);
        updateDiagnosesInModel(model, lookupDiagnosesRepository, AutorisationUtils.getCurrentUserMid());
        updateReasonsInModel(model, lookupPurposeRepository);
        customerFinancialInfo.updateCustomerAndFinancialInfo(model, customerRepository.findById(customerId).orElseThrow());

        Appointment appointment = Appointment.builder().visitDateTime(
                        (creationDate != null) ? creationDate : LocalDateTime.now()
                )
                .localMemberId(
                        (localMemberId != null) ? localMemberId : AutorisationUtils.getCurrentUserMlid()
                )
                .build();
        LocalMemberPreferences prefMemberData = objectMapper.readValue(AutorisationUtils.getCurrentLocalMember().getMetaLocalMemberPreferences().getPreferencesJson(), LocalMemberPreferences.class);
        model
                .addAttribute("selectedRoom", AgendaTypeEnum.Room.equals(agendaType) ? resource : "")
                .addAttribute("selectedVet", AgendaTypeEnum.Vet.equals(agendaType) ? resource : "")
                .addAttribute("appointment", appointment)
                .addAttribute("localMembersList", AutorisationUtils.getLocalMemberList())
                .addAttribute("staffList", userService.getStaffMembers(AutorisationUtils.getCurrentUserMid()))
                .addAttribute("timeList", IntStream.rangeClosed(1, 24).map(i -> i * 5).mapToObj(i -> new PresentationElement((long) i, String.valueOf(i))).toList())
                .addAttribute("localMemberDefaultTimeNeeded", prefMemberData.getEstimatedTime())
                .addAttribute("salesType", SalesType.VISIT)
        ;
        if (getHtmxAndAddToModel(request, model)) {
            model.addAttribute("isHtmx", true);
            return "salesconsult-generic-module/petanddateselectpagemodal";
        } else {
            return "salesconsult-generic-module/petanddateselectpage";
        }
    }

    @PostMapping("/visit/search/{customerId}")
    String thirdStepPetFoundCreateAppointmentAndVisit(@NotNull @PathVariable Long customerId, @NotNull CreateVisitForm createVisitForm, Model model, HttpServletRequest request) {

        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        List<AppointmentVisitService.CreatePet> pets = createVisitForm.formPet().stream().filter(pet -> pet.checked() != null && pet.checked()).toList();
        if (customer == null || pets.isEmpty()) {
            model.addAttribute("message", "Something went wrong. Please try again");
            return "redirect:/visit/search/" + customerId;
        }
        Appointment app = appointmentVisitService.createAppointmentVisit(pets, customerId, SalesType.VISIT);

        if (getHtmxAndAddToModel(request, model)) {
            model.addAttribute("flatData", app.getId());
            return "fragments/elements/flatData";
        } else {
            return "redirect:/consult/visit/customer/" + customerId + "/visit/" + app.getVisits().iterator().next().getId() + "?callFrom=customer";
        }
    }

    // List visits from customer/customer
    @GetMapping("/visit/customer/{customerId}/visits")
    String getVisitsForCustomer(@NotNull @PathVariable Long customerId, Model model) {
        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        model
                .addAttribute("customerId", customer.id())
                .addAttribute("pets", customer.pets().stream().collect(Collectors.toMap(CustomerService.Pet::id, p -> p.deceased() ? p.name() + " &dagger;" : p.name())))
                .addAttribute("visits", visitRepository.findByMemberIdAndPet_IdInOrderByAppointment_VisitDateTimeDesc(AutorisationUtils.getCurrentUserMid(), customer.pets().stream().map(CustomerService.Pet::id).toList()))
                .addAttribute("localMembersList", AutorisationUtils.getLocalMemberMap())
                .addAttribute("activeMenu", "visits");
        return "consult-module/visit/list";
    }

    @PostMapping("/visit/customer/{customerId}/visit/{visitId}")
    String saveVisitXhtm(@NotNull @PathVariable Long customerId, @NotNull @PathVariable Long visitId, Visit visitForm, HttpServletResponse response, Locale locale) {
        customerService.searchCustomer(customerId);
        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow();
        updateVisit(visit, visitForm);
        visitRepository.save(visit);
        response.setHeader("HX-Trigger", "{\"messageDisplay\":{\"messageText\":\""
                + messageSource.getMessage("label.saved", null, locale)
                + "\"}}");
        return "fragments/elements/empty";
    }

    @GetMapping("/visit/customer/{customerId}/appointment/{appointmentId}/pet/{petId}")
    String EditVisitFromReminderScreen(@NotNull @PathVariable Long customerId, @NotNull @PathVariable Long appointmentId,@NotNull  @PathVariable Long petId, @RequestParam String callFrom, Model model) {
        Visit visit = visitRepository.findByAppointment_IdAndPet_Id(appointmentId, petId).orElseThrow();
        return editVisit( customerId, visit.getId(), callFrom,  model);
    }

    @GetMapping("/visit/customer/{customerId}/visit/{visitId}")
    String editVisit(@NotNull @PathVariable Long customerId, @NotNull @PathVariable Long visitId, @RequestParam String callFrom, Model model) {

        CustomerService.Customer customer = customerService.searchCustomer(customerId);
        Visit visit = visitRepository.findByMemberIdAndId(AutorisationUtils.getCurrentUserMid(), visitId).orElseThrow();

        LocalMemberPreferences pref = objectMapper.readValue(AutorisationUtils.getCurrentLocalMember().getMetaLocalMemberPreferences().getPreferencesJson(), LocalMemberPreferences.class);

        updateLineItemsInModel(model, lineItemService.getLineItemsForPet(visit.getPet().getId(), visit.getAppointment().getId()));
        updateReasonsInModel(model, lookupPurposeRepository);
        updateRoomsInModel(model, lookupRoomRepository);
        updateLocationsInModel(model, lookupLocationRepository);
        updateDiagnosesInModel(model, lookupDiagnosesRepository,AutorisationUtils.getCurrentUserMid());
        updatePetDiagnosesInModel(model, diagnoseRepository, AutorisationUtils.getCurrentUserMid(), visit.getPet().getId(), visit.getAppointment().getId());
        updateVisitStatusInModel(model, visit.getStatus(), SalesType.VISIT);

        customerFinancialInfo.updateCustomerAndFinancialInfo(model, customerRepository.findById(customerId).orElseThrow());

        List<Appointment> app =
                switch (callFrom) {
                    case "agenda" -> appointmentRepository.findByVisitDateTimeBetweenAndOTCAndLocalMemberId(
                            LocalDate.now().atStartOfDay(), LocalDate.now().atTime(LocalTime.MAX), YesNoEnum.No, AutorisationUtils.getCurrentUserMlid()
                    );
                    case "customer" -> appointmentRepository.findByMemberIdAndVisits_Id(AutorisationUtils.getCurrentUserMid(), customerId);
                    default -> throw new IllegalArgumentException("Unsupported callFrom: " + callFrom);
                };

        List<AnalyseItem> analyseItems = analyseItemRepository.findByMemberIdAndAppointmentIdAndPetId(AutorisationUtils.getCurrentUserMid(), visit.getAppointment().getId(), visit.getPet().getId());
        model
                .addAttribute("appointmentList", app)
                .addAttribute("callFrom", callFrom)
                .addAttribute("userIsAllowed", AutorisationUtils.getCurrentUserIsAuthorized())
                .addAttribute("customer", customer)
                .addAttribute("visit", visit)
                .addAttribute("appointment", visit.getAppointment())
                .addAttribute("petsOnAppointment", visit.getAppointment().getVisits().stream()
                        .sorted(Comparator.comparing(v -> v.getPet().getNameWithDeceased()))
                        .map(vist -> new PresentationElement(vist.getId(), vist.getPet().getNameWithDeceased())).toList())

                .addAttribute("categoryNames", productService.getCategoriesWithoutDeletedRecs())
                .addAttribute("productSearchUrl", VISIT_URL.replace("{customerId}", customer.id().toString()).replace("{visitId}", visit.getId().toString()))
                .addAttribute("productSearchForm", new ProductController.ListForm(null,null, Boolean.FALSE))

                .addAttribute("templates", pref.getConsultTextTemplate(objectMapper))
                .addAttribute("ynvaluesList", YesNoEnum.getWebList())
                .addAttribute("staffList", userService.getStaffMembers(AutorisationUtils.getCurrentUserMid()))

                // the analyse descriptions will be available when there is a description selected on the visit screen.
                .addAttribute("analyseDescription", analyseDescriptionRepository.findByMemberId(AutorisationUtils.getCurrentUserMid()))
                .addAttribute("analyses", analyseDescriptionRepository.findByMemberId(AutorisationUtils.getCurrentUserMid()))
                .addAttribute("analyseItems", analyseItems)
                .addAttribute("isAnalyseItemsFromDb", !analyseItems.isEmpty())
                .addAttribute("attachedPayment", visit.getPaymentVisits().isEmpty() ? null : visit.getPaymentVisits().iterator().next().getPayment())
        ;


//        for the view:
//        'analyseItems' is filled initially by the saved version;
//        if no records found, then the dropdown is displayed, and the selection is made to load the analyses that, after selecting, can be saved
        return "consult-module/visit/action";
    }


    public record CreateVisitForm(List<AppointmentVisitService.CreatePet> formPet, Appointment appointment) {
    }

    private void updateVisit(Visit visit, Visit visitForm) {
        visit.setPurpose(visitForm.getPurpose());

        visit.setVeterinarian(visitForm.getVeterinarian());
        visit.setRoom(visitForm.getRoom());
        visit.setSentToInsurance(visitForm.getSentToInsurance());

        visit.setWeight(visitForm.getWeight());
        visit.setTemperature(visitForm.getTemperature());
        visit.setGlucose(visitForm.getGlucose());
        visit.setComments(visitForm.getComments());
    }

    private static boolean getHtmxAndAddToModel(HttpServletRequest request, Model model) {
        final boolean isHtmx = "true".equals(request.getHeader("HX-Request"));
        model.addAttribute("isHtmx", isHtmx);
        return isHtmx;
    }
}