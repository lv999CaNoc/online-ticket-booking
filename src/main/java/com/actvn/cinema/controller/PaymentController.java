package com.actvn.cinema.controller;

import com.actvn.cinema.DTO.OrderDTO;
import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Bill;
import com.actvn.cinema.model.Schedule;
import com.actvn.cinema.model.Ticket;
import com.actvn.cinema.repositories.BillRepository;
import com.actvn.cinema.service.BillService;
import com.actvn.cinema.service.PaypalService;
import com.actvn.cinema.service.TicketService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private BillService billService;
    private BillRepository billRepository;
    private TicketService ticketService;
    private PaypalService service;

    @GetMapping("/checkout")
    public String getMovieCheckout(@RequestParam("bid") Integer billId, Model model) {
        Schedule schedule = null;
        try {
            List<Ticket> tickets = ticketService.getTicketsByBillId(billId);
            model.addAttribute("tickets", tickets);

            schedule = tickets.get(0).getSchedule();
            model.addAttribute("schedule", schedule);
        } catch (NotFoundException e) {
            // Bug
        }

        model.addAttribute("billId", billId);
        model.addAttribute("movie", schedule.getMovie());
        model.addAttribute("room", schedule.getRoom());
        return "payment-checkout";
    }

    @GetMapping("/success")
    public String paymentSuccess(@RequestParam("bid") Integer billId,
                                 @RequestParam("paymentId") String paymentId,
                                 @RequestParam("PayerID") String payerId,
                                 RedirectAttributes ra) {
        try {
            Payment payment = service.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                Bill bill = billService.getBillById(billId);
                bill.setStatus("COMPLETE");
                billRepository.save(bill);
                ra.addFlashAttribute("message", "success");
                System.out.println("payment success");
            }
        } catch (NotFoundException | PayPalRESTException e) {
            ra.addFlashAttribute("message", "failure");
        }
        return "redirect:/payment/checkout?bid=" + billId;
    }

    @GetMapping("/cancel")
    public String cancel(@RequestParam("bid") Integer billId, RedirectAttributes ra) {
        ra.addFlashAttribute("message", "failure");
        return "redirect:/payment/checkout?bid=" + billId;
    }

    @PostMapping("/process")
    public String payment(@ModelAttribute("order") OrderDTO order, RedirectAttributes ra) {
        try {
            Payment payment = service.createPayment(order.getPrice(), "USD", "paypal",
                    "sale", order.getDescription(), "http://localhost:8080/payment/cancel?bid=" + order.getBillId(),
                    "http://localhost:8080/payment/success?bid=" + order.getBillId());
            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    return "redirect:" + link.getHref();
                }
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        ra.addFlashAttribute("message", "failure");
        return "redirect:/payment/checkout";
    }
}
