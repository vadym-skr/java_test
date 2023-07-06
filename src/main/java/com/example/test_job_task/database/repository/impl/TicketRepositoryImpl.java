package com.example.test_job_task.database.repository.impl;

import com.example.test_job_task.database.entity.Ticket;
import com.example.test_job_task.database.repository.BaseRepository;
import com.example.test_job_task.database.repository.TicketRepository;
import com.example.test_job_task.exception.InternalViolationException;
import com.example.test_job_task.exception.InternalViolationType;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class TicketRepositoryImpl extends BaseRepositoryImpl<Ticket> implements TicketRepository {

    public TicketRepositoryImpl() {
        super(new HashMap<>());
    }

    public TicketRepositoryImpl(Map<Long, Ticket> data) {
        super(data);
    }

    @Override
    public boolean isReservedTicket(Long id) {
        if (!data.containsKey(id)) {
            throw new InternalViolationException(InternalViolationType.TICKET_WITH_SUCH_ID_NOT_FOUND);
        }
        return data.get(id).isReserved();
    }
}
