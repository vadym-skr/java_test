package com.example.test_job_task.database.repository.impl;

import com.example.test_job_task.database.entity.Coupon;
import com.example.test_job_task.database.entity.Ticket;
import com.example.test_job_task.database.repository.CouponRepository;
import com.example.test_job_task.database.repository.TicketRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class CouponRepositoryImpl extends BaseRepositoryImpl<Coupon> implements CouponRepository {

    public CouponRepositoryImpl() {
        super(new HashMap<>());
    }

    public CouponRepositoryImpl(Map<Long, Coupon> data) {
        super(data);
    }
}
