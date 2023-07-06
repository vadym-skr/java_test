package com.example.test_job_task;

import com.example.test_job_task.database.repository.BaggageRepository;
import com.example.test_job_task.database.repository.CouponRepository;
import com.example.test_job_task.database.repository.DestinationRepository;
import com.example.test_job_task.database.repository.TicketRepository;
import com.example.test_job_task.exception.InternalViolationException;
import com.example.test_job_task.exception.InternalViolationType;
import com.example.test_job_task.service.impl.TicketServiceImpl;
import com.example.test_job_task.util.CashManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
//@RunWith(SpringJUnit4ClassRunner.class)
class TestJobTaskApplicationTests {

    @Mock
    private CashManager cashManager;
    @Mock
    private CouponRepository couponRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private BaggageRepository baggageRepository;
    @Mock
    private DestinationRepository destinationRepository;

    private TicketServiceImpl ticketService;

    @BeforeEach
    void preparing() {
        this.ticketService = new TicketServiceImpl(cashManager, couponRepository, ticketRepository, baggageRepository, destinationRepository);
    }

    @Test
    void testIsReservedIfNotFoundAndCashNotContains() {
        Long ticketId = 1L;
        String cashMessage = "isReserved{id=1}";

        Mockito.when(ticketRepository.isReservedTicket(ticketId))
            .thenThrow(new InternalViolationException(InternalViolationType.TICKET_WITH_SUCH_ID_NOT_FOUND));
        Mockito.when(cashManager.getFromCache(cashMessage)).thenReturn(null);

        assertThatThrownBy(() -> ticketService.isReserved(ticketId))
            .isInstanceOf(InternalViolationException.class)
            .hasMessage(InternalViolationType.TICKET_WITH_SUCH_ID_NOT_FOUND.getMessage());

        verify(ticketRepository, times(1)).isReservedTicket(ticketId);
        verify(cashManager, times(1)).getFromCache(cashMessage);
    }

}
