package com.example.test_job_task;

import com.example.test_job_task.database.repository.BaggageRepository;
import com.example.test_job_task.database.repository.CouponRepository;
import com.example.test_job_task.database.repository.DestinationRepository;
import com.example.test_job_task.database.repository.TicketRepository;
import com.example.test_job_task.dto.request.ReserveTicketRequest;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
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
    void testIsReserved_IfCashNotContainsAndNotFoundTicket() {
        Long ticketId = 1L;
        String cashKey = "isReserved{id=1}";

        Mockito.when(ticketRepository.isReservedTicket(ticketId))
            .thenThrow(new InternalViolationException(InternalViolationType.TICKET_WITH_SUCH_ID_NOT_FOUND));
        Mockito.when(cashManager.getFromCache(cashKey)).thenReturn(null);

        assertThatThrownBy(() -> ticketService.isReserved(ticketId))
            .isInstanceOf(InternalViolationException.class)
            .hasMessage(InternalViolationType.TICKET_WITH_SUCH_ID_NOT_FOUND.getMessage());

        verify(ticketRepository, times(1)).isReservedTicket(ticketId);
        verify(cashManager, times(1)).getFromCache(cashKey);
        verify(cashManager, times(0)).addToCache(eq(cashKey), anyBoolean());
    }

    @Test
    void testIsReserved_IfCashContains() {
        Long ticketId = 1L;
        String cashKey = "isReserved{id=1}";
        boolean expectedResult = true;

        Mockito.when(cashManager.getFromCache(cashKey)).thenReturn(expectedResult);

        boolean result = ticketService.isReserved(ticketId);

        assertEquals(expectedResult, result);

        verify(ticketRepository, times(0)).isReservedTicket(anyLong());
        verify(cashManager, times(1)).getFromCache(cashKey);
    }

    @Test
    void testIsReserved_IfCashNotContainsAndTicketNotReserved() {
        Long ticketId = 1L;
        String cashKey = "isReserved{id=1}";
        boolean expectedResult = false;

        Mockito.when(ticketRepository.isReservedTicket(ticketId))
            .thenReturn(false);
        Mockito.when(cashManager.getFromCache(cashKey)).thenReturn(null);

        boolean result = ticketService.isReserved(ticketId);

        assertEquals(expectedResult, result);

        verify(ticketRepository, times(1)).isReservedTicket(ticketId);
        verify(cashManager, times(1)).getFromCache(cashKey);
        verify(cashManager, times(1)).addToCache(cashKey, false);
    }

    @Test
    void testReserve_ifTicketNotExists() {
        Long ticketId = 1L;
        Long baggageId = 1L;
        Long destinationId = 1L;
        ReserveTicketRequest request = new ReserveTicketRequest(ticketId, baggageId, destinationId);

        Mockito.when(ticketRepository.existsById(ticketId))
            .thenReturn(false);

        assertThatThrownBy(() -> ticketService.reserve(request))
            .isInstanceOf(InternalViolationException.class)
            .hasMessage(InternalViolationType.TICKET_WITH_SUCH_ID_NOT_FOUND.getMessage());

        verify(ticketRepository, times(1)).existsById(ticketId);
    }

    @Test
    void testReserve_ifBaggageNotExists() {
        Long ticketId = 1L;
        Long baggageId = 1L;
        Long destinationId = 1L;
        ReserveTicketRequest request = new ReserveTicketRequest(ticketId, baggageId, destinationId);

        Mockito.when(ticketRepository.existsById(ticketId))
            .thenReturn(true);
        Mockito.when(baggageRepository.existsById(baggageId))
            .thenReturn(false);

        assertThatThrownBy(() -> ticketService.reserve(request))
            .isInstanceOf(InternalViolationException.class)
            .hasMessage(InternalViolationType.BAGGAGE_WITH_SUCH_ID_NOT_FOUND.getMessage());

        verify(ticketRepository, times(1)).existsById(ticketId);
        verify(baggageRepository, times(1)).existsById(ticketId);
    }

    @Test
    void testReserve_ifDestinationNotFound() {
        Long ticketId = 1L;
        Long baggageId = 1L;
        Long destinationId = 1L;
        ReserveTicketRequest request = new ReserveTicketRequest(ticketId, baggageId, destinationId);

        Mockito.when(ticketRepository.existsById(ticketId))
            .thenReturn(true);
        Mockito.when(baggageRepository.existsById(baggageId))
            .thenReturn(true);
        Mockito.when(destinationRepository.existsById(destinationId))
            .thenReturn(false);

        assertThatThrownBy(() -> ticketService.reserve(request))
            .isInstanceOf(InternalViolationException.class)
            .hasMessage(InternalViolationType.DESTINATION_WITH_SUCH_ID_NOT_FOUND.getMessage());

        verify(ticketRepository, times(1)).existsById(ticketId);
        verify(baggageRepository, times(1)).existsById(ticketId);
        verify(destinationRepository, times(1)).existsById(ticketId);
    }

    @Test
    void testReserve_ifTicketIsAlreadyReserved() {
        Long ticketId = 1L;
        Long baggageId = 1L;
        Long destinationId = 1L;
        ReserveTicketRequest request = new ReserveTicketRequest(ticketId, baggageId, destinationId);

        Mockito.when(ticketRepository.existsById(ticketId))
            .thenReturn(true);
        Mockito.when(baggageRepository.existsById(baggageId))
            .thenReturn(true);
        Mockito.when(destinationRepository.existsById(destinationId))
            .thenReturn(true);
        Mockito.when(ticketRepository.isReservedTicket(ticketId))
            .thenReturn(true);

        assertThatThrownBy(() -> ticketService.reserve(request))
            .isInstanceOf(InternalViolationException.class)
            .hasMessage(InternalViolationType.TICKET_WITH_SUCH_ID_RESERVED.getMessage());

        verify(ticketRepository, times(1)).existsById(ticketId);
        verify(baggageRepository, times(1)).existsById(ticketId);
        verify(destinationRepository, times(1)).existsById(ticketId);
        verify(ticketRepository, times(1)).isReservedTicket(ticketId);
    }
}
