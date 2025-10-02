package dwe.holding.generic.admin.autorisation;

import dwe.holding.generic.admin.autorisation.user.UserRepository;
import dwe.holding.generic.admin.model.User;
import dwe.holding.generic.admin.security.AutorisationUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserRepository userRepository;


    private final String start =  "redirect:/index";

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    String newScreen() {
        return "admin-module/login";
    }

    @GetMapping("/index")
    String indexScreen() {
        if (AutorisationUtils.isNewUser()) {
            return "redirect:/resetpassword";
        }

        if (AutorisationUtils.isLocalMemberRequired() && AutorisationUtils.getCurrentUserMlid() == null) {
            return "redirect:/" + AutorisationUtils.getCurrentMember().getApplicationName().toLowerCase() + "/userpreferences";
        }

        if (AutorisationUtils.getCurrentMember().getShortCode().equals("ZVS")) {
            return AutorisationUtils.getCurrentMember().getApplicationRedirect();
        }
        return start;
    }

    @GetMapping("/resetpassword")
    String resetPassword(Model model) {
        model.addAttribute("userId", AutorisationUtils.getCurrentUserId());
        return "admin-module/resetpassword";
    }

    @PostMapping("/resetpassword")
    String resetPassword(PasswordForm form, Model model) {
        if (form.password.equals(form.password2) && AutorisationUtils.getCurrentUserId().toString().equals(form.id())) {
            model.addAttribute("error", "not correct");
            return "/admin-module/resetpassword";
        } else {
            User user = userRepository.findById(AutorisationUtils.getCurrentUserId()).get();
            user.setPassword(passwordEncoder.encode(form.password));
            user.setChangePassword(false);
            User savedUser = userRepository.save(user);
            AutorisationUtils.setCurrentUser(savedUser);
            return start;
        }
    }

    @GetMapping("/favicon.ico")
    @ResponseBody
    void returnNoFavicon() {
    }


    @PostMapping("/error")
    String error() {
        return "admin-module/error";
    }

    record PasswordForm(String id, String password, String password2) {
    }

}