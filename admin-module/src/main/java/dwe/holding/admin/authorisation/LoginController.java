package dwe.holding.admin.authorisation;

import dwe.holding.admin.authorisation.tenant.user.UserRepository;
import dwe.holding.admin.model.tenant.User;
import dwe.holding.admin.sessionstorage.AutorisationUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin")
/**
 *  We do some validation and hop around to be sure all data is validated and/or set
 *  the following pages are hit in order:
 *  /admin/login <- show the page with extra shortcode field
 *  /index  <- do stuff for the specific application; pending on if localmember is required
 *    /application/start <- if we do not have any localmember requirment
 *    /application/userpreferences <- if we do have a localmember requirment
 * /
 */
public class LoginController {

    final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserRepository localMemberRepository;

    public LoginController(UserRepository localMemberRepository) {
        this.localMemberRepository = localMemberRepository;
    }

    @GetMapping("/login")
    String newScreen() {
        return "admin-module/login";
    }


    @GetMapping("/index")
    String indexScreen() {
        if (AutorisationUtils.isNewUser()) {
            return "redirect:/admin/resetpassword";
        }

        if (AutorisationUtils.isLocalMemberRequired() && (AutorisationUtils.getCurrentUserMlid() == 0 || AutorisationUtils.getCurrentUserMlid() == null)) {
            return "redirect:/" + AutorisationUtils.getCurrentMember().getApplicationName().toLowerCase() + "/userpreferences";
        }
        return AutorisationUtils.getCurrentMember().getApplicationRedirect();
    }


    @GetMapping("/resetpassword")
    String resetPassword(Model model) {
        model.addAttribute("userId", AutorisationUtils.getCurrentUserId());
        return "admin-module/resetpassword";
    }

    @PostMapping("/resetpassword")
    String resetPassword(ResetPasswordForm form, Model model) {
        if (form.password.equals(form.password2) && AutorisationUtils.getCurrentUserId().toString().equals(form.id())) {
            model.addAttribute("error", "not correct");
            return "/admin-module/resetpassword";
        }

        User user = localMemberRepository.findById(AutorisationUtils.getCurrentUserId()).orElseThrow();
        if (!passwordEncoder.matches(form.currentPassword(), user.getPassword())){
            model.addAttribute("error", "not correct");
            return "/admin-module/resetpassword";
        }
        user.setPassword(passwordEncoder.encode(form.password));
        user.setChangePassword(false);
        User savedUser = localMemberRepository.save(user);
        AutorisationUtils.setCurrentUser(savedUser);
        return "redirect:/admin/index";

    }

    @GetMapping("/favicon.ico")
    @ResponseBody
    void returnNoFavicon() {
    }

    @GetMapping("/keepalive")
    @ResponseBody
    void returnNoAction() {
    }


    @PostMapping("/error")
    String error() {
        return "admin-module/error";
    }

    record ResetPasswordForm(String id, String currentPassword, String password, String password2) {
    }
}