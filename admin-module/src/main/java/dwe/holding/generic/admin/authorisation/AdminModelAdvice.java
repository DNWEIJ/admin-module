package dwe.holding.generic.admin.authorisation;

import dwe.holding.generic.admin.authorisation.function_role.FunctionController;
import dwe.holding.generic.admin.authorisation.function_role.RoleController;
import dwe.holding.generic.admin.authorisation.member.LocalMemberController;
import dwe.holding.generic.admin.authorisation.member.MemberController;
import dwe.holding.generic.admin.authorisation.user.UserController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = {FunctionController.class, RoleController.class,
        MemberController.class, LocalMemberController.class, UserController.class})
public class AdminModelAdvice {

    @ModelAttribute
    void modelAdvice(Model model) {
        model.addAttribute("applicationName", "admin-module");
    }
}