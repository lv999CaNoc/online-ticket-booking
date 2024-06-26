package com.actvn.cinema.controller;

import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Branch;
import com.actvn.cinema.model.Room;
import com.actvn.cinema.repositories.RoomRepository;
import com.actvn.cinema.service.BranchService;
import com.actvn.cinema.service.RoomService;
import com.actvn.cinema.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/room")
public class RoomController {
    @Autowired
    private RoomService roomService;
    @Autowired
    private SeatService seatService;
    @Autowired
    private BranchService branchService;
    @Autowired
    private RoomRepository roomRepository;

    @GetMapping("/search")
    public String search(@RequestParam("search") String search, Model model, RedirectAttributes ra) {
        try {
            List<Room> rooms = roomService.findRoomByNameContaining(search);
            model.addAttribute("rooms", rooms);
            model.addAttribute("pageTitle", "Tìm kiếm phòng");
            return "admin/management-room";
        } catch (NotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/dashboard/management-room";
        }
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        try {
            List<Branch> listBranch = branchService.listAll();
            model.addAttribute("listBranch", listBranch);
        } catch (NotFoundException e) {
            model.addAttribute("listBranchEmpty", e.getMessage());
        }

        model.addAttribute("room", new Room());
        model.addAttribute("pageTitle", "Thêm phòng mới");
        return "admin/room-form";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Integer id, Model model,
                               RedirectAttributes ra) {
        try {
            Room room = roomService.get(id);
            List<Branch> listBranch = branchService.listAll();
            model.addAttribute("listBranch", listBranch);

            model.addAttribute("room", room);
            model.addAttribute("pageTitle", "Cập nhật " + room.getName() + " - Rạp " + room.getBranch().getName());
            return "admin/room-form";
        } catch (NotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/dashboard/management-room";
        }
    }

    @PostMapping("/save")
    public String save(Room room, RedirectAttributes ra) {
        try {
            if (room.getId()==null){
                // create new room
                roomService.save(room);
                seatService.initSeat(room);
            }else{
                roomService.update(room);
            }
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/room/new";
        }
        ra.addFlashAttribute("successMessage", "Phòng " + room.getName() + " đã lưu thành công.");
        return "redirect:/dashboard/management-room";
    }

    @GetMapping("delete")
    public String delete(@RequestParam("id") Integer id, RedirectAttributes ra) {
        try {
            roomService.delete(id);
            ra.addFlashAttribute("successMessage", "Xóa phòng thành công.");
        } catch (NotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard/management-room";
    }
}
