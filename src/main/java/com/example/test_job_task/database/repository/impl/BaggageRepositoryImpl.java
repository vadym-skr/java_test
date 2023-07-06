package com.example.test_job_task.database.repository.impl;

import com.example.test_job_task.database.entity.Baggage;
import com.example.test_job_task.database.repository.BaggageRepository;
import com.example.test_job_task.database.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class BaggageRepositoryImpl extends BaseRepositoryImpl<Baggage> implements BaggageRepository {

    public BaggageRepositoryImpl() {
        super(new HashMap<>());
    }

    public BaggageRepositoryImpl(Map<Long, Baggage> data) {
        super(data);
    }
}
