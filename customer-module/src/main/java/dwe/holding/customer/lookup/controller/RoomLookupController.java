package dwe.holding.customer.lookup.controller;

import dwe.holding.customer.client.model.lookup.LookupRoom;
import dwe.holding.customer.lookup.repository.RoomLookupRepository;
import dwe.holding.generic.admin.security.AutorisationUtils;
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
public class RoomLookupController {
    private final RoomLookupRepository roomLookupRepository;

    public RoomLookupController(RoomLookupRepository roomLookupRepository) {
        this.roomLookupRepository = roomLookupRepository;
    }

    @GetMapping("lookup/rooms")
    String list(Model model) {
        model.addAttribute("rooms", roomLookupRepository.getByMemberId(77L)); // todo replace with
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
        LookupRoom rooms = roomLookupRepository.findById(roomId).orElseThrow();
        model.addAttribute("room", rooms.getMemberId().equals(77L) ? rooms : new LookupRoom());
        model.addAttribute("activeMenu", "room");
        ; //AutorisationUtils.getCurrentUserMid()
        return "customer-module/lookup/rooms/action";
    }

    @PostMapping("lookup/room")
    @Transactional
    String saveRecord(LookupRoom formRoom, RedirectAttributes redirect) {
        if (formRoom.isNew()) {
            roomLookupRepository.save(
                    LookupRoom.builder()
                            .room(formRoom.getRoom())
                            .build()
            );
        } else {
            LookupRoom room = roomLookupRepository.findById(formRoom.getId()).orElseThrow();
            if (room.getMemberId().equals(AutorisationUtils.getCurrentUserMid())) {
                room.setRoom(formRoom.getRoom());
                roomLookupRepository.save(room);
            } else {
                redirect.addFlashAttribute("message", "Something went wrong. Please try again");
            }
        }
        return "redirect:/customer/lookup/rooms";
    }
}