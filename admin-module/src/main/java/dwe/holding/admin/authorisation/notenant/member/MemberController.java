package dwe.holding.admin.authorisation.notenant.member;

import dwe.holding.admin.model.notenant.Member;
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

@Controller
@Validated
@RequestMapping("/admin")
public class MemberController {
    private final MemberRepository memberRepository;

    public MemberController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @PostMapping("/member")
    String save(@Valid Member member, BindingResult bindingResult, Model model, RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "admin-module/member/action";
        }
        memberRepository.save(member);
        redirect.addFlashAttribute("message", "label.saved");
        return "redirect:/admin/members";
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
    String showEditScreen(@PathVariable @NotNull   Long id, Model model) {
        model.addAttribute("action", "Edit");
        model.addAttribute("member", memberRepository.findById(id).orElseThrow());
        setModelData(model);
        return "admin-module/member/action";
    }

    @GetMapping("/members")
    String listScreen(Model model) {
        model.addAttribute("action", "List");
        model.addAttribute("members", memberRepository.findAll());
        return "admin-module/member/list";
    }

    private void setModelData(Model model) {
        model.addAttribute("ynvaluesList", YesNoEnum.getWebList());
    }
}