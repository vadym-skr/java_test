package com.example.test_job_task.database.repository.impl;

import com.example.test_job_task.database.entity.Baggage;
import com.example.test_job_task.database.entity.Destination;
import com.example.test_job_task.database.repository.BaseRepository;
import com.example.test_job_task.database.repository.DestinationRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class DestinationRepositoryImpl extends BaseRepositoryImpl<Destination> implements DestinationRepository {

    public DestinationRepositoryImpl() {
        super(new HashMap<>());
    }

    public DestinationRepositoryImpl(Map<Long, Destination> data) {
        super(data);
    }
}
