package dwe.holding.salesconsult.consult.controller;

import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.salesconsult.consult.model.LookupRoom;
import dwe.holding.salesconsult.consult.repository.LookupRoomRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(path = "/customer")
@Slf4j
// TODO: Move to admin module or shared-module? This isn't customer...
public class LookupRoomController {
    private final LookupRoomRepository lookupRoomRepository;

    public LookupRoomController(LookupRoomRepository lookupRoomRepository) {
        this.lookupRoomRepository = lookupRoomRepository;
    }

    @GetMapping("lookup/rooms")
    String list(Model model) {
        model.addAttribute("rooms", lookupRoomRepository.getByMemberIdOrderByRoom(AutorisationUtils.getCurrentUserMid()));
        model.addAttribute("activeMenu", "room");
        return "customer-module/lookup/rooms/list";
    }

    @GetMapping("lookup/room")
    String newRecord(Model model) {
        model.addAttribute("room", new LookupRoom());
        model.addAttribute("activeMenu", "room");
        return "customer-module/lookup/rooms/action";
    }

    @GetMapping("lookup/room/{roomId}")
    String editRecord(@PathVariable Long roomId, Model model) {
        LookupRoom rooms = lookupRoomRepository.findById(roomId).orElseThrow();
        model.addAttribute("room", rooms.getMemberId().equals(AutorisationUtils.getCurrentUserMid()) ? rooms : new LookupRoom());
        model.addAttribute("activeMenu", "room");
        ; //AutorisationUtils.getCurrentUserMid()
        return "customer-module/lookup/rooms/action";
    }

    @PostMapping("lookup/room")
    @Transactional
    String saveRecord(LookupRoom formRoom, RedirectAttributes redirect) {
        if (formRoom.isNew()) {
            lookupRoomRepository.save(
                    LookupRoom.builder()
                            .room(formRoom.getRoom())
                            .build()
            );
        } else {
            LookupRoom room = lookupRoomRepository.findById(formRoom.getId()).orElseThrow();
            if (room.getMemberId().equals(AutorisationUtils.getCurrentUserMid())) {
                room.setRoom(formRoom.getRoom());
                lookupRoomRepository.save(room);
            } else {
                redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            }
        }
        return "redirect:/customer/lookup/rooms";
    }
}