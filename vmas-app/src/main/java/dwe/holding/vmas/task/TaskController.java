package dwe.holding.vmas.task;


import dwe.holding.admin.expose.UserService;
import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.customer.client.controller.form.CustomerForm;
import dwe.holding.salesconsult.consult.repository.LookupRoomRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import static dwe.holding.salesconsult.sales.controller.ModelHelper.updateRoomsInModel;


@Controller
@RequestMapping("task")
@AllArgsConstructor
public class TaskController {
    private final UserService userService;
    private final CustomerForm customerForm;
    private final LookupRoomRepository lookupRoomRepository;

    @RequestMapping("new")
    String showTaskScreen(Model model) {
        updateRoomsInModel(model, lookupRoomRepository);
        model
                .addAttribute("staffList", userService.getStaffMembers(AutorisationUtils.getCurrentUserMid()))
                .addAttribute("localMembersList", AutorisationUtils.getLocalMemberList())
                .addAttribute("form", customerForm)
                .addAttribute("customerSearchUrl", "/task/new/customer")
                .addAttribute("taskForm", new TaskForm());
        return "/agenda-module/newtask";
    }

    record TaskForm() {
    }
}
