package action.v3;

import model.Ticket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import service.TicketServiceSpring;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    @Mock
    private TicketServiceSpring service;

    @Mock
    private HttpServletRequest request;

    @Mock(lenient = true)  // delete does not return a body so mock gives UnnecessaryStubbingException
    private HttpServletResponse response;

    @InjectMocks
    private TicketController controller;

    private ByteArrayOutputStream responseBody;

    @BeforeEach
    void setUp() throws Exception {

        responseBody = new ByteArrayOutputStream();

        ServletOutputStream outputStream = new ServletOutputStream() {

            @Override
            public void write(int b) throws IOException {
                responseBody.write(b);
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener listener) {
                // Not needed for this unit test
            }
        };

        Mockito.when(response.getOutputStream()).thenReturn(outputStream);

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

        Mockito.when(service.getTickets())
                .thenReturn(Collections.singletonList(ticket));

        controller.index();

        Mockito.verify(service).getTickets();
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

        Long id = 1L;
        controller.setId(id.toString());

        Ticket ticket = controller.getModel();

        ticket.setId(id);
        ticket.setSummary("Updated summary");
        ticket.setAssignee("Robert");
        ticket.setStatus("CLOSED");

        Mockito.when(service.updateTicket(id, ticket)).thenReturn(ticket);

        controller.update();

        Mockito.verify(service).updateTicket(id, ticket);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);

        Assertions.assertTrue(responseBody.toString().contains("Updated summary"));
    }

    @Test
    void destroyDeletesTicket() throws Exception {

        Long id = 1L;
        Mockito.when(service.deleteTicket(id)).thenReturn(true);

        controller.setId(id.toString());

        controller.destroy();

        Mockito.verify(service).deleteTicket(id);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void indexReturnsEmptyArrayWhenNoTicketsExist() throws Exception {

        Mockito.when(service.getTickets())
                .thenReturn(Collections.emptyList());

        controller.index();

        Mockito.verify(service).getTickets();

        Assertions.assertEquals("[]", responseBody.toString().trim());
    }
}
