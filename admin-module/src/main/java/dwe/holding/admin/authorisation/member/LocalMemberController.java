package dwe.holding.admin.authorisation.member;

import dwe.holding.admin.model.LocalMember;
import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



import static dwe.holding.admin.security.ButtonConstants.getRedirectFor;

@Controller
@Validated
@RequestMapping("/admin")
public class LocalMemberController {
    private final LocalMemberRepository localMemberRepository;

    public LocalMemberController(LocalMemberRepository localMemberRepository) {
        this.localMemberRepository = localMemberRepository;
    }

    @PostMapping("/localmember")
    String save(@Valid LocalMember localMember, BindingResult bindingResult, Model model, RedirectAttributes redirect, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "admin-module/localmember/action";
        }
          Long memberLocalId = processMemberLocal(localMember);
        redirect.addFlashAttribute("message", "Local Member saved successfully!");
        return getRedirectFor(request, memberLocalId, "redirect:/localmember");
    }

    @GetMapping("/localmember")
    String newScreen(Model model) {
        model.addAttribute("action", "Create");
        setModelData(model, new LocalMember());
        return "admin-module/localmember/action";
    }


    @GetMapping("/localmember/{id}")
    String showEditScreen(@PathVariable @NotNull   Long id, Model model) {
        model.addAttribute("action", "Edit");
        setModelData(model, localMemberRepository.findById(id).orElseThrow());
        return "admin-module/localmember/action";
    }

    @GetMapping("/localmember/list")
    String listScreen(Model model) {
        model.addAttribute("action", "List");
        model.addAttribute("members", localMemberRepository.findAll());
        return "admin-module/localmember/list";
    }

    private void setModelData(Model model, LocalMember local) {
        model.addAttribute("ynvaluesList", YesNoEnum.getWebList());
        model.addAttribute("localmember", local);
    }

    private   Long processMemberLocal(LocalMember localMember) {
        localMember.setMid(AutorisationUtils.getCurrentUserMid());
        LocalMember savedLocalMember = localMemberRepository.save(localMember);
        return savedLocalMember.getId();
    }
}