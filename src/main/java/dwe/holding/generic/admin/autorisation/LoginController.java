package dwe.holding.generic.admin.autorisation;

import dwe.holding.generic.admin.autorisation.user.UserRepository;
import dwe.holding.generic.admin.exception.ApplicationException;
import dwe.holding.generic.admin.model.User;
import dwe.holding.generic.admin.security.AutorisationUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    String newScreen() {
        return "admin-module/login";
    }

    @GetMapping("/index")
    String indexScreen() throws ApplicationException {
        if (AutorisationUtils.isNewUser()) {
            return "redirect:/resetpassword";
        }
        return "/index";
    }

    @GetMapping("/resetpassword")
    String resetPassword(Model model) {
        model.addAttribute("userId", AutorisationUtils.getCurrentUserId());
        return "admin-module/resetpassword";
    }

    @PostMapping("/resetpassword")
    String resetPassword(PasswordForm form, Model model) {
        if (form.password.equals(form.password2) && AutorisationUtils.getCurrentUserId().equals(form.id())) {
            model.addAttribute("error", "not correct");
            return "/admin-module/resetpassword";
        } else {
            User user = userRepository.findById(AutorisationUtils.getCurrentUserId()).get();
            user.setPassword(passwordEncoder.encode(form.password));
            user.setChangePassword(false);
            User savedUser = userRepository.save(user);
            AutorisationUtils.setCurrentUser(savedUser);
            return "/index";
        }
    }

    @PostMapping("/error")
    String error() {

        return "admin-module/error";
    }

    record PasswordForm(String id, String password, String password2) {
    }
}