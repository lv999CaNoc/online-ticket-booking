package com.actvn.cinema.controller;

import com.actvn.cinema.exception.RoomNotFoundException;
import com.actvn.cinema.model.Branch;
import com.actvn.cinema.model.Room;
import com.actvn.cinema.repositories.RoomRepository;
import com.actvn.cinema.service.BranchService;
import com.actvn.cinema.service.RoomService;
import com.actvn.cinema.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
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
    public String search(@RequestParam("search") String search
            , Model model, RedirectAttributes ra){
        try{
            List<Room> rooms = roomService.findRoomByNameContaining(search);
            model.addAttribute("rooms", rooms);
            model.addAttribute("pageTitle","Tìm kiếm phòng");
            return "admin/management-room";
        } catch (RoomNotFoundException roomNotFoundException) {
            ra.addFlashAttribute("errorMessage", roomNotFoundException.getMessage());
            return "redirect:/dashboard/management-room";
        }
    }

    @GetMapping("/new")
    public String add(Model model){
        List<Branch> listBranch = branchService.listAll();
        model.addAttribute("listBranch", listBranch);
        model.addAttribute("room", new Room());

        model.addAttribute("pageTitle", "Thêm phòng mới");
        return "admin/room-form";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Integer id, Model model,
                               RedirectAttributes ra){
        try {
            Room room = roomService.get(id);
            List<Branch> listBranch = branchService.listAll();
            model.addAttribute("listBranch", listBranch);

            model.addAttribute("room", room);
            model.addAttribute("pageTitle", "Cập nhật "+room.getName()+". Rạp "+room.getBranch().getName());
            return "admin/room-form";
        } catch (RoomNotFoundException roomNotFoundException) {
            ra.addFlashAttribute("errorMessage", roomNotFoundException.getMessage());
            return "redirect:/dashboard/management-room";
        }
    }

    @PostMapping("/save")
    public String save(Room room, RedirectAttributes ra,
            final BindingResult bindingResult, Model model){
        if (room.getCapacity()==null){
            bindingResult.addError(new FieldError("room", "capacity", "Sức chứa không được để trống"));
        }
        if (room.getBranch()==null){
            bindingResult.addError(new FieldError("room", "branch", "Rạp không được để trống"));
        }
        Room existedRoom = roomRepository.findRoomByNameContainingIgnoreCase(room.getName()).get(0);
        if (existedRoom != null){
            if ( existedRoom.getId() != room.getId() && existedRoom.getBranch() == room.getBranch()){
                bindingResult.addError(new FieldError("room", "name", room.getName() + " đã tồn tại trong rạp "+ room.getBranch().getName()));
            }
        }

        if (bindingResult.hasErrors()){
            List<Branch> listBranch = branchService.listAll();
            model.addAttribute("listBranch", listBranch);
            return "admin/room-form";
        }

        // update exist room
        roomService.save(room);
        if (room.getId() == null){
            // create new room
            seatService.initSeat(room);
        }

        ra.addFlashAttribute("successMessage",
                room.getName()+" đã lưu thành công.");
        return "redirect:/dashboard/management-room";
    }

    @GetMapping("delete")
    public String delete(@RequestParam("id") Integer id, RedirectAttributes ra){
        try {
            roomService.delete(id);
            ra.addFlashAttribute("successMessage",
                    "Xóa phòng thành công.");
        } catch (RoomNotFoundException roomNotFoundException) {
            ra.addFlashAttribute("errorMessage", roomNotFoundException.getMessage());
        }
        return "redirect:/dashboard/management-room";
    }
}
