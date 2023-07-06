package com.example.test_job_task.controller;

import com.example.test_job_task.dto.request.AddCouponTicketRequest;
import com.example.test_job_task.dto.request.ReserveTicketRequest;
import com.example.test_job_task.service.TicketService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    public boolean isReserved(Long id) {
        return ticketService.isReserved(id);
    }

    public boolean reserve(ReserveTicketRequest request) {
        return ticketService.reserve(request);
    }

    public boolean addCoupon(AddCouponTicketRequest request) {
        return ticketService.addCoupon(request);
    }
}
