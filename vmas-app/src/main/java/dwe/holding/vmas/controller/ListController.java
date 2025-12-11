package dwe.holding.vmas.controller;

import dwe.holding.admin.security.TenantAccessDecisionVoter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@AllArgsConstructor
public class ListController {

    public final TenantAccessDecisionVoter tenantAccessDecisionVoter;

    @GetMapping("/urilist")
    @ResponseBody
    String getList(){
        return String.join("<br>", tenantAccessDecisionVoter.getList());
    }
}