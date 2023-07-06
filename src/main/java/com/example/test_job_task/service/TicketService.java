package com.example.test_job_task.service;

import com.example.test_job_task.dto.request.AddCouponTicketRequest;
import com.example.test_job_task.dto.request.ReserveTicketRequest;

public interface TicketService {
    boolean isReserved(Long id);

    boolean reserve(ReserveTicketRequest request);

    boolean addCoupon(AddCouponTicketRequest request);
}
