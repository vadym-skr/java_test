package com.example.test_job_task.dto.request;

public class AddCouponTicketRequest {

    private Long ticketId;

    private Long couponId;

    public AddCouponTicketRequest() {
    }

    public AddCouponTicketRequest(Long ticketId, Long couponId) {
        this.ticketId = ticketId;
        this.couponId = couponId;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }
}
