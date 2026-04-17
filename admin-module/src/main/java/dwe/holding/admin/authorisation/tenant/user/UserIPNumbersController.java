package dwe.holding.admin.authorisation.tenant.user;

import dwe.holding.admin.authorisation.IPNumber;
import dwe.holding.admin.authorisation.tenant.user.mapper.UserMapper;
import dwe.holding.admin.model.tenant.IPSecurity;
import dwe.holding.admin.model.tenant.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@Validated
@RequestMapping("/admin/user")
@AllArgsConstructor
public class UserIPNumbersController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;;

    @GetMapping("/{userId}/ips")
    String getListOfIps(@PathVariable Long userId, Model model) {
        model.addAttribute("ips", userRepository.findById(userId).orElseThrow().getIpNumbers());
        return "";
    }

    @GetMapping("/{userId}/ip")
    String getNewIp(@PathVariable Long userId, Model model) {
        model.addAttribute("ip", new IPNumber());
        return "admin-module/user/ipmodal";
    }

    @PostMapping("/{userId}/ip")
    String saveNewIp(@PathVariable Long userId, IPNumber ipnumber, Model model) {
        User user = userRepository.findById(userId).orElseThrow();
       user.getIpNumbers().add(
               IPSecurity.builder().userId(user.getId()).ipnumber(ipnumber.toString()).build()
       );
       userRepository.save(user);
        return "admin-module/user/ipmodal";
    }

    @DeleteMapping("/{userId}/ip")
    String deleteIp(@PathVariable Long userId, IPNumber ipnumber, Model model) {
        User user = userRepository.findById(userId).orElseThrow();
        user.getIpNumbers().remove(
               user.getIpNumbers().stream().filter(ip -> ip.equals(ipnumber)).findFirst().get()
        );
        return "admin-module/user/ipmodal";
    }


}