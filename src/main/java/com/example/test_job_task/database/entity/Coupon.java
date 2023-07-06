package com.example.test_job_task.database.entity;

public class Coupon extends BaseEntity {

    private Long id;

    // discount in percentage (10.00, 12.50, etc)
    private Float discount;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }
}
