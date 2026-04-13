package dwe.holding.admin.authorisation.tenant.user;

import dwe.holding.admin.authorisation.notenant.member.MemberRepository;
import dwe.holding.admin.authorisation.tenant.role.RoleRepository;
import dwe.holding.admin.authorisation.tenant.user.mapper.UserMapper;
import dwe.holding.admin.model.base.BaseBO;
import dwe.holding.admin.model.tenant.Role;
import dwe.holding.admin.model.tenant.User;
import dwe.holding.admin.model.tenant.UserRole;
import dwe.holding.admin.model.type.LanguagePrefEnum;
import dwe.holding.admin.model.type.PersonnelStatusEnum;
import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.shared.model.type.YesNoEnum;
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

import java.util.*;
import java.util.stream.Collectors;

@Controller
@Validated
@RequestMapping("/admin/user")
@AllArgsConstructor
public class UserIPNumbersController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;;

    @GetMapping("{id}/ips")
    String getListOfIps(Long id, Model model) {
        model.addAttribute("ips", userRepository.findById(id).orElseThrow().getIpNumbers().stream());
        return "";
    }

}