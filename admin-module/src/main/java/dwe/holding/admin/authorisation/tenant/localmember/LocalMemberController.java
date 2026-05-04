package dwe.holding.admin.authorisation.tenant.localmember;

import dwe.holding.admin.model.tenant.LocalMember;
import dwe.holding.admin.model.tenant.LocalMemberTax;
import dwe.holding.admin.model.tenant.MetaLocalMemberPreferences;
import dwe.holding.admin.model.type.AgendaTypeEnum;
import dwe.holding.admin.preferences.LocalMemberPreferences;
import dwe.holding.admin.preferences.Template;
import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.shared.model.type.PaymentMethodEnum;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

@Controller
@Validated
@RequestMapping("/admin")
@AllArgsConstructor
public class LocalMemberController {
    private final LocalMemberRepository localMemberRepository;
    private final ObjectMapper objectMapper;

    @GetMapping("/localmembers")
    String listScreen(Model model) {
        model.addAttribute("action", "List");
        model.addAttribute("localmembers", localMemberRepository.findByMemberId(AutorisationUtils.getCurrentUserMid()));
        return "admin-module/localmember/list";
    }

    @GetMapping("/localmember")
    String newScreen(Model model) {
        model.addAttribute("action", "Create");
        setModelData(model, LocalMember.builder().metaLocalMemberPreferences(new MetaLocalMemberPreferences()).build());
        return "admin-module/localmember/action";
    }

    @GetMapping("/localmember/{id}")
    String showEditScreen(@PathVariable @NotNull Long id, Model model) {
        model.addAttribute("action", "Edit");
        setModelData(model, localMemberRepository.findById(id).orElseThrow());
        model.addAttribute("paymentList", PaymentMethodEnum.getWebList());
        return "admin-module/localmember/action";
    }

    @GetMapping("/localmember/{id}/consulttemplate")
    String addAConsultTemplate(@PathVariable @NotNull Long id, Model model) {
        model.addAttribute("action", "Edit");
        LocalMember local = localMemberRepository.findById(id).orElseThrow();
        LocalMemberPreferences pref = objectMapper.readValue(local.getMetaLocalMemberPreferences().getPreferencesJson(), LocalMemberPreferences.class);
        List<Template> list = pref.getConsultTextTemplate(objectMapper);
        list.add(new Template(list.size() + 1, "", "", false));
        model
                .addAttribute("templates", list)
                .addAttribute("ynvaluesList", YesNoEnum.getWebList())
                .addAttribute("localmember", local)
                .addAttribute("pref", pref);
        return "admin-module/localmember/action";
    }

    @PostMapping("/localmember")
    String save(@Valid LocalMember localMemberForm, RedirectAttributes redirect) {
        processMemberLocal(localMemberForm);
        redirect.addFlashAttribute("message", "label.saved");
        return "redirect:/admin/localmembers";
    }

    @PostMapping("/localmember/{localmemberId}/tax")
    String saveTax(@PathVariable Long localmemberId, BigDecimal taxLow, BigDecimal taxHigh, Model model) {
        LocalMember local = localMemberRepository.findById(localmemberId).orElseThrow();
        LocalDate dateBorder = LocalDate.of(2999, 12, 31);
        LocalMemberTax borderTax = local.getMemberLocalTaxs().stream().filter(tax -> tax.getEndDate().equals(dateBorder)).findFirst().orElseThrow();
        borderTax.setEndDate(LocalDate.now());
        local.getMemberLocalTaxs().add(
                LocalMemberTax.builder()
                        .startDate(LocalDate.now()).endDate(dateBorder).taxLow(taxLow).taxHigh(taxHigh).localMember(local)
                .build());

        model.addAttribute("localmember", localMemberRepository.save(local));
        return "/admin-module/localmember/snippets/taxtable";
    }


    private void setModelData(Model model, LocalMember local) {
        LocalMemberPreferences pref = objectMapper.readValue(local.getMetaLocalMemberPreferences().getPreferencesJson(), LocalMemberPreferences.class);
        model
                .addAttribute("ynvaluesList", YesNoEnum.getWebList())
                .addAttribute("localmember", local)
                .addAttribute("pref", pref)
                .addAttribute("paymentList", PaymentMethodEnum.getWebList())
                .addAttribute("templates", pref.getConsultTextTemplate(objectMapper))
                .addAttribute("agendaTypeList", AgendaTypeEnum.getWebList())
                .addAttribute("yesNoList", YesNoEnum.getWebList())
                .addAttribute("timeList", IntStream.rangeClosed(1, 24).map(i -> i * 5).mapToObj(i -> new PresentationElement(String.valueOf(i), String.valueOf(i))).toList())
// todo move preference to vmas (user and LocalMember)
                .addAttribute("rooms", List.of())
// lookupRoomRepository.findByLocalMemberIdAndMemberId(AutorisationUtils.getCurrentUserMlid(), AutorisationUtils.getCurrentUserMid()) )
        ;
    }

    private void processMemberLocal(LocalMember localMemberForm) {
        LocalMember localMember = localMemberRepository.findById(localMemberForm.getId()).orElseThrow();
        localMember.setLocalMemberName(localMemberForm.getLocalMemberName());
        localMember.setEmail(localMemberForm.getEmail());
        localMember.setPhone1(localMemberForm.getPhone1());
        localMember.setPhone2(localMemberForm.getPhone2());
        localMember.setStreetAndNr(localMemberForm.getStreetAndNr());
        localMember.setCity(localMemberForm.getCity());
        localMember.setState(localMemberForm.getState());
        localMember.setZipCode(localMemberForm.getZipCode());
        // preferences update to json before storing
        localMemberForm.getPref().setConsultTextTemplate(objectMapper);
        localMember.getMetaLocalMemberPreferences().setPreferencesJson(localMemberForm.getPrefJson(objectMapper));
        localMemberRepository.save(localMember);
    }
}