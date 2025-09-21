package dwe.holding.generic.admin.autorisation;

import dwe.holding.generic.admin.autorisation.member.LocalMemberRepository;
import dwe.holding.generic.admin.autorisation.user.UserRepository;
import dwe.holding.generic.admin.exception.ApplicationException;
import dwe.holding.generic.admin.model.PresentationFunction;
import dwe.holding.generic.admin.model.User;
import dwe.holding.generic.admin.security.AutorisationUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Comparator;
import java.util.UUID;

@Controller
public class LoginController {

    final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserRepository userRepository;
    private final LocalMemberRepository localMemberRepository;

    public LoginController(UserRepository userRepository, LocalMemberRepository localMemberRepository) {
        this.userRepository = userRepository;
        this.localMemberRepository = localMemberRepository;
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
        return "admin-module/index";
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
            if (AutorisationUtils.isLocalMemberRequired() && savedUser.getMemberLocalId() == null) {
                return "redirect:/setlocalmember";
            }
            return "admin-module/index";
        }
    }

    @GetMapping("/setlocalmember")
    String localMember(Model model) {
        model.addAttribute("localMembersList",
                localMemberRepository.findByMember_Id(AutorisationUtils.getCurrentUserMid())
                        .stream().map(
                                f -> new PresentationFunction(f.getId(), f.getLocalMemberName(), true)
                        )
                        .sorted(Comparator.comparing(PresentationFunction::getName)).toList()
        );
        return "admin-module/setlocalmember";
    }

    @PostMapping("/setlocalmember")
    String localMember(LocalMemberForm form, Model model) {
        User user = userRepository.findById(AutorisationUtils.getCurrentUserId()).get();
        user.setMemberLocalId(UUID.fromString(form.id));
        User savedUser = userRepository.save(user);
        AutorisationUtils.setCurrentUser(savedUser);
        return "admin-module/index";
    }


    @PostMapping("/error")
    String error() {
        return "admin-module/error";
    }

    record PasswordForm(String id, String password, String password2) {
    }

    record LocalMemberForm(String id) {
    }
}