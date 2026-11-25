package action.v2;

import model.Ticket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import service.TicketService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    @Mock
    private TicketService service;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private TicketController controller;

    private StringWriter responseBody;

    @BeforeEach
    void setUp() throws Exception {

        responseBody = new StringWriter();

        Mockito.when(response.getWriter())
                .thenReturn(new PrintWriter(responseBody));

        controller.setServletRequest(request);
        controller.setServletResponse(response);
    }

    @Test
    void indexReturnsAllTickets() throws Exception {

        Ticket ticket = new Ticket(
                1L,
                "Fix login issue",
                "Robert",
                "OPEN"
        );

        Mockito.when(service.getTickets("OPEN", "Robert"))
                .thenReturn(Collections.singletonList(ticket));

        controller.index();

        Mockito.verify(service).getTickets("OPEN", "Robert");
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);

        Assertions.assertTrue(responseBody.toString().contains("Fix login issue"));
    }

    @Test
    void showReturnsTicket() throws Exception {

        Ticket ticket = new Ticket(
                1L,
                "Fix login issue",
                "Robert",
                "OPEN"
        );

        controller.setId("1");

        Mockito.when(service.getTicket(1L)).thenReturn(ticket);

        controller.show();

        Mockito.verify(service).getTicket(1L);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);

        Assertions.assertTrue(responseBody.toString().contains("Fix login issue"));
    }

    @Test
    void createCreatesTicket() throws Exception {

        Ticket ticket = controller.getModel();

        ticket.setSummary("New ticket");
        ticket.setAssignee("Robert");
        ticket.setStatus("OPEN");

        Ticket savedTicket = new Ticket(
                10L,
                "New ticket",
                "Robert",
                "OPEN"
        );

        Mockito.when(service.createTicket(ticket)).thenReturn(savedTicket);

        controller.create();

        Mockito.verify(service).createTicket(ticket);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);

        Assertions.assertTrue(responseBody.toString().contains("New ticket"));
    }

    @Test
    void updateUpdatesTicket() throws Exception {

        controller.setId("1");

        Ticket ticket = controller.getModel();

        ticket.setSummary("Updated summary");
        ticket.setAssignee("Robert");
        ticket.setStatus("CLOSED");

        Mockito.when(service.updateTicket(1L, ticket)).thenReturn(true);

        controller.update();

        Mockito.verify(service).updateTicket(1L, ticket);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);

        Assertions.assertTrue(responseBody.toString().contains("Updated summary"));
    }

    @Test
    void destroyDeletesTicket() throws Exception {

        controller.setId("1");

        controller.destroy();

        Mockito.verify(service).deleteTicket(1L);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void indexReturnsEmptyArrayWhenNoTicketsExist() throws Exception {

        Mockito.when(service.getTickets(null, null))
                .thenReturn(Collections.emptyList());

        controller.index();

        Mockito.verify(service).getTickets(null, null);

        Assertions.assertEquals("[]", responseBody.toString().trim());
    }
}
