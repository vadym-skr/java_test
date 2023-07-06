package com.example.test_job_task.database.repository;

import com.example.test_job_task.database.entity.Ticket;

public interface TicketRepository extends BaseRepository<Ticket> {

    boolean isReservedTicket(Long id);
}
