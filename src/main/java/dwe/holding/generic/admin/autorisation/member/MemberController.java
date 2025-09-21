package dwe.holding.generic.admin.autorisation.member;

import dwe.holding.generic.admin.model.Member;
import dwe.holding.generic.admin.model.type.YesNoEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static dwe.holding.generic.admin.security.ButtonConstants.getRedirectFor;

@Controller
@Validated
public class MemberController {
    private final MemberRepository memberRepository;

    public MemberController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @PostMapping("/member")
    String save(@Valid Member member, BindingResult bindingResult, Model model, RedirectAttributes redirect, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "admin-module/member/action";
        }
        Member savedMember = memberRepository.save(member);
        redirect.addFlashAttribute("message", "Member saved successfully!");
        return getRedirectFor(request, savedMember.getId(), "redirect:/member");
    }

    @GetMapping("/member")
    String newScreen(Model model) {
        model.addAttribute("action", "Create");
        model.addAttribute("member", new Member());
        setModelData(model);
        model.addAttribute("ynvaluesList", YesNoEnum.getWebList());
        return "admin-module/member/action";
    }


    @GetMapping("/member/{id}")
    String showEditScreen(@PathVariable @NotNull Long id, Model model) {
        model.addAttribute("action", "Edit");
        model.addAttribute("member", memberRepository.findById(id).orElseThrow());
        setModelData(model);
        return "admin-module/member/action";
    }

    @GetMapping("/member/list")
    String listScreen(Model model) {
        model.addAttribute("action", "List");
        model.addAttribute("members", memberRepository.findAll());
        return "admin-module/member/list";
    }

    private void setModelData(Model model) {
        model.addAttribute("ynvaluesList", YesNoEnum.getWebList());
    }
}