package com.example.test_job_task;

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
import com.example.test_job_task.service.impl.TicketServiceImpl;
import com.example.test_job_task.util.CashManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @Test
    void testReserve_ifAllExistAndTicketNotReservedAndCashNotContain() {
        Long ticketId = 1L;
        Long baggageId = 1L;
        Long destinationId = 1L;
        ReserveTicketRequest request = new ReserveTicketRequest(ticketId, baggageId, destinationId);
        String cashKey = "isReserved{id=1}";
        Ticket ticketFromRep = new Ticket();
        ticketFromRep.setId(1L);
        ticketFromRep.setPrice(10.50);
        ticketFromRep.setReserved(false);

        Mockito.when(ticketRepository.existsById(ticketId))
            .thenReturn(true);
        Mockito.when(baggageRepository.existsById(baggageId))
            .thenReturn(true);
        Mockito.when(destinationRepository.existsById(destinationId))
            .thenReturn(true);
        Mockito.when(ticketRepository.isReservedTicket(ticketId))
            .thenReturn(false);
        Mockito.when(ticketRepository.getById(ticketId))
            .thenReturn(ticketFromRep);
        Mockito.when(ticketRepository.update(any(Ticket.class)))
            .thenReturn(true);
        Mockito.when(cashManager.isInCache(cashKey)).thenReturn(false);

        boolean result = ticketService.reserve(request);

        assertTrue(result);

        verify(ticketRepository, times(1)).existsById(ticketId);
        verify(baggageRepository, times(1)).existsById(ticketId);
        verify(destinationRepository, times(1)).existsById(ticketId);
        verify(ticketRepository, times(1)).isReservedTicket(ticketId);
        verify(ticketRepository, times(1)).getById(ticketId);
        verify(ticketRepository, times(1)).update(any(Ticket.class));
        verify(cashManager, times(1)).isInCache(cashKey);
    }

    @Test
    void testReserve_ifAllExistAndTicketNotReservedAndCashContains() {
        Long ticketId = 1L;
        Long baggageId = 1L;
        Long destinationId = 1L;
        ReserveTicketRequest request = new ReserveTicketRequest(ticketId, baggageId, destinationId);
        String cashKey = "isReserved{id=1}";
        Ticket ticketFromRep = new Ticket();
        ticketFromRep.setId(1L);
        ticketFromRep.setPrice(10.50);
        ticketFromRep.setReserved(false);

        Mockito.when(ticketRepository.existsById(ticketId))
            .thenReturn(true);
        Mockito.when(baggageRepository.existsById(baggageId))
            .thenReturn(true);
        Mockito.when(destinationRepository.existsById(destinationId))
            .thenReturn(true);
        Mockito.when(ticketRepository.isReservedTicket(ticketId))
            .thenReturn(false);
        Mockito.when(ticketRepository.getById(ticketId))
            .thenReturn(ticketFromRep);
        Mockito.when(ticketRepository.update(any(Ticket.class)))
            .thenReturn(true);
        Mockito.when(cashManager.isInCache(cashKey)).thenReturn(true);

        boolean result = ticketService.reserve(request);

        assertTrue(result);

        verify(ticketRepository, times(1)).existsById(ticketId);
        verify(baggageRepository, times(1)).existsById(ticketId);
        verify(destinationRepository, times(1)).existsById(ticketId);
        verify(ticketRepository, times(1)).isReservedTicket(ticketId);
        verify(ticketRepository, times(1)).getById(ticketId);
        verify(ticketRepository, times(1)).update(any(Ticket.class));
        verify(cashManager, times(1)).isInCache(cashKey);
        verify(cashManager, times(1)).addToCache(cashKey, true);
    }

    @Test
    void testAddCoupon_ifTicketNotExists() {
        Long ticketId = 1L;
        Long couponId = 1L;
        AddCouponTicketRequest request = new AddCouponTicketRequest(ticketId, couponId);

        Mockito.when(ticketRepository.existsById(ticketId))
            .thenReturn(false);

        assertThatThrownBy(() -> ticketService.addCoupon(request))
            .isInstanceOf(InternalViolationException.class)
            .hasMessage(InternalViolationType.TICKET_WITH_SUCH_ID_NOT_FOUND.getMessage());

        verify(ticketRepository, times(1)).existsById(ticketId);
    }

    @Test
    void testAddCoupon_ifTicketExistsButCouponNotExists() {
        Long ticketId = 1L;
        Long couponId = 1L;
        AddCouponTicketRequest request = new AddCouponTicketRequest(ticketId, couponId);

        Mockito.when(ticketRepository.existsById(ticketId))
            .thenReturn(true);
        Mockito.when(couponRepository.existsById(ticketId))
            .thenReturn(false);

        assertThatThrownBy(() -> ticketService.addCoupon(request))
            .isInstanceOf(InternalViolationException.class)
            .hasMessage(InternalViolationType.COUPON_WITH_SUCH_ID_NOT_FOUND.getMessage());

        verify(ticketRepository, times(1)).existsById(ticketId);
        verify(couponRepository, times(1)).existsById(couponId);
    }

    @Test
    void testAddCoupon_ifAllExistsButTicketNotReserved() {
        Long ticketId = 1L;
        Long couponId = 1L;
        AddCouponTicketRequest request = new AddCouponTicketRequest(ticketId, couponId);
        Ticket ticketFromRep = new Ticket();
        ticketFromRep.setId(1L);
        ticketFromRep.setPrice(10.50);
        ticketFromRep.setReserved(false);
        Coupon couponFromRep = new Coupon();
        couponFromRep.setId(1L);
        couponFromRep.setDiscount(10.00F);

        Mockito.when(ticketRepository.existsById(ticketId))
            .thenReturn(true);
        Mockito.when(couponRepository.existsById(ticketId))
            .thenReturn(true);
        Mockito.when(ticketRepository.getById(ticketId))
            .thenReturn(ticketFromRep);
        Mockito.when(couponRepository.getById(ticketId))
            .thenReturn(couponFromRep);

        assertThatThrownBy(() -> ticketService.addCoupon(request))
            .isInstanceOf(InternalViolationException.class)
            .hasMessage(InternalViolationType.TICKET_WITH_SUCH_ID_NOT_RESERVED.getMessage());

        verify(ticketRepository, times(1)).existsById(ticketId);
        verify(couponRepository, times(1)).existsById(couponId);
        verify(ticketRepository, times(1)).getById(ticketId);
        verify(couponRepository, times(1)).getById(couponId);
    }

    @Test
    void testAddCoupon_ifEverythingFine() {
        Long ticketId = 1L;
        Long couponId = 1L;
        AddCouponTicketRequest request = new AddCouponTicketRequest(ticketId, couponId);
        Ticket ticketFromRep = new Ticket();
        ticketFromRep.setId(1L);
        ticketFromRep.setPrice(10.00);
        ticketFromRep.setReserved(true);
        Coupon couponFromRep = new Coupon();
        couponFromRep.setId(1L);
        couponFromRep.setDiscount(10.00F);

        double expectedPrice = 9.0;

        Mockito.when(ticketRepository.existsById(ticketId))
            .thenReturn(true);
        Mockito.when(couponRepository.existsById(ticketId))
            .thenReturn(true);
        Mockito.when(ticketRepository.getById(ticketId))
            .thenReturn(ticketFromRep);
        Mockito.when(couponRepository.getById(ticketId))
            .thenReturn(couponFromRep);
        Mockito.when(ticketRepository.update(any(Ticket.class)))
            .thenReturn(true);

        boolean result = ticketService.addCoupon(request);

        assertTrue(result);

        verify(ticketRepository, times(1)).existsById(ticketId);
        verify(couponRepository, times(1)).existsById(couponId);
        verify(ticketRepository, times(1)).getById(ticketId);
        verify(couponRepository, times(1)).getById(couponId);
        verify(ticketRepository, times(1)).update(any(Ticket.class));
        verify(ticketRepository).update(argThat(
            ticket ->
                ticket.getPrice().equals(10.00)
                && ticket.getFinalPrice().equals(expectedPrice)
                && ticket.getCouponId().equals(couponId)
        ));
    }
}
