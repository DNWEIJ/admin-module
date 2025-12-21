package dwe.holding.vmas.controller;


import dwe.holding.admin.expose.UserService;
import dwe.holding.admin.model.tenant.LocalMember;
import dwe.holding.admin.preferences.LocalMemberPreferences;
import dwe.holding.admin.security.AutorisationUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tools.jackson.databind.ObjectMapper;

@RequestMapping("/vmas")
@Controller
@AllArgsConstructor
public class IndexController {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @GetMapping("/index")
    String index(Model model) {
        LocalMemberPreferences pref = objectMapper.readValue(AutorisationUtils.getCurrentLocalMember().getMetaLocalMemberPreferences().getPreferencesJson(), LocalMemberPreferences.class);
        model
                .addAttribute("comment", pref.getFirstPageMessage())
                .addAttribute("localMemberId", AutorisationUtils.getCurrentUserMlid())
                .addAttribute("localMembersList", AutorisationUtils.getLocalMemberList());
        return "vmas-module/index";
    }

    @PostMapping("/index")
    String changeLocalmember(Long localMemberId, Model model, RedirectAttributes redirectAttributes) {
        // validate memeberID
        LocalMember localMember = AutorisationUtils.getCurrentMember().getLocalMembers().stream().filter(f -> f.getId().equals(localMemberId)).findFirst().orElseThrow();
        AutorisationUtils.setCurrentUser(userService.setLocalMemberId(localMember.getId()));
        redirectAttributes.addFlashAttribute("message", "Successfully changed local member");
        return "redirect:/vmas/index";
    }

}