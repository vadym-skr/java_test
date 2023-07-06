package com.example.test_job_task.service.impl;

import com.example.test_job_task.database.entity.Coupon;
import com.example.test_job_task.database.entity.Ticket;
import com.example.test_job_task.database.repository.BaggageRepository;
import com.example.test_job_task.database.repository.CouponRepository;
import com.example.test_job_task.database.repository.DestinationRepository;
import com.example.test_job_task.database.repository.TicketRepository;
import com.example.test_job_task.dto.request.AddCouponTicketRequest;
import com.example.test_job_task.dto.request.ReserveTicketRequest;
import com.example.test_job_task.exception.InternalViolationException;
import com.example.test_job_task.exception.InternalViolationType;
import com.example.test_job_task.service.TicketService;
import com.example.test_job_task.util.CashManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TicketServiceImpl implements TicketService {

    private final CashManager cashManager;

    private final CouponRepository couponRepository;
    private final TicketRepository ticketRepository;
    private final BaggageRepository baggageRepository;
    private final DestinationRepository destinationRepository;

    @Autowired
    public TicketServiceImpl(CashManager cashManager, CouponRepository couponRepository, TicketRepository ticketRepository, BaggageRepository baggageRepository, DestinationRepository destinationRepository) {
        this.cashManager = cashManager;
        this.couponRepository = couponRepository;
        this.ticketRepository = ticketRepository;
        this.baggageRepository = baggageRepository;
        this.destinationRepository = destinationRepository;
    }

    @Override
    public boolean isReserved(Long id) {
        String cashName = "isReserved{id=" + id + "}";
        Boolean cashResult = (Boolean) cashManager.getFromCache(cashName);
        if (cashResult != null) {
            return cashResult;
        }
        boolean result = ticketRepository.isReservedTicket(id);
        cashManager.addToCache(cashName, result);
        return result;
    }

    @Override
    public boolean reserve(ReserveTicketRequest request) {
        if (!ticketRepository.existsById(request.getTicketId())) {
            throw new InternalViolationException(InternalViolationType.TICKET_WITH_SUCH_ID_NOT_FOUND);
        }
        if (!baggageRepository.existsById(request.getBaggageId())) {
            throw new InternalViolationException(InternalViolationType.BAGGAGE_WITH_SUCH_ID_NOT_FOUND);
        }
        if (!destinationRepository.existsById(request.getDestinationId())) {
            throw new InternalViolationException(InternalViolationType.DESTINATION_WITH_SUCH_ID_NOT_FOUND);
        }
        if (ticketRepository.isReservedTicket(request.getTicketId())) {
            throw new InternalViolationException(InternalViolationType.TICKET_WITH_SUCH_ID_RESERVED);
        }

        Ticket ticket = ticketRepository.getById(request.getTicketId());
        ticket.setBaggageId(request.getBaggageId());
        ticket.setDestinationId(request.getDestinationId());
        ticket.setReserved(true);

        // cash
        // update exists cash if it exists
        String cashName = "isReserved{id=" + request.getTicketId() + "}";
        if (cashManager.isInCache(cashName)) {
            cashManager.addToCache(cashName, true);
        }
        // cash

        return ticketRepository.update(ticket);
    }

    @Override
    public boolean addCoupon(AddCouponTicketRequest request) {
        if (ticketRepository.isReservedTicket(request.getTicketId())) {
            throw new InternalViolationException(InternalViolationType.TICKET_WITH_SUCH_ID_RESERVED);
        }
        if (!couponRepository.existsById(request.getCouponId())) {
            throw new InternalViolationException(InternalViolationType.COUPON_WITH_SUCH_ID_NOT_FOUND);
        }

        Coupon coupon = couponRepository.getById(request.getCouponId());
        Ticket ticket = ticketRepository.getById(request.getTicketId());

        if (!ticket.isReserved()) {
            throw new InternalViolationException(InternalViolationType.TICKET_WITH_SUCH_ID_NOT_RESERVED);
        }

        double finalPrice = ticket.getPrice() - ((coupon.getDiscount() / 100) * ticket.getPrice());

        ticket.setCouponId(request.getCouponId());
        ticket.setFinalPrice(finalPrice);

        return ticketRepository.update(ticket);
    }
}
