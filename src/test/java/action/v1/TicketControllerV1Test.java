package action.v1;

import model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.TicketService;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketControllerV1Test {

    @Mock
    private TicketService service;

    @Mock
    private HttpServletRequest request;

    @Mock(lenient = true)
    private HttpServletResponse response;

    private TestableTicketController controller;
    private ByteArrayOutputStream responseBody;

    // Override database initialization so tests don't connect to PostgreSQL.
    private static class TestableTicketController extends TicketController {

        @Override
        protected void initializeDatabase() {
            // No database initialization during unit tests.
        }
    }

    @BeforeEach
    void setUp() throws Exception {

        controller = new TestableTicketController();

        // Replace the real service with our Mockito mock.
        controller.setService(service);

        controller.setServletRequest(request);
        controller.setServletResponse(response);

        responseBody = new ByteArrayOutputStream();

        ServletOutputStream outputStream = new ServletOutputStream() {

            @Override
            public void write(int b) {
                responseBody.write(b);
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener listener) {
            }
        };

        when(response.getOutputStream()).thenReturn(outputStream);
    }

    private void setRequestBody(String json) throws Exception {

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        ServletInputStream inputStream = new ServletInputStream() {

            private final ByteArrayInputStream input =
                    new ByteArrayInputStream(bytes);

            @Override
            public int read() {
                return input.read();
            }

            @Override
            public boolean isFinished() {
                return input.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {
            }
        };

        when(request.getInputStream()).thenReturn(inputStream);
    }

    private String getResponseBody() {
        return new String(
                responseBody.toByteArray(),
                StandardCharsets.UTF_8
        );
    }

    @Test
    void getAllTickets() throws Exception {

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/v1/tickets");
        when(request.getParameter("status")).thenReturn("OPEN");
        when(request.getParameter("assignee")).thenReturn("Robert");

        Ticket ticket = new Ticket(
                1L, "Fix login issue", "Robert", "OPEN"
        );

        when(service.getTickets("OPEN", "Robert"))
                .thenReturn(Collections.singletonList(ticket));

        controller.execute();

        verify(service).getTickets("OPEN", "Robert");
        verify(response).setStatus(200);

        assertTrue(getResponseBody().contains("Fix login issue"));
    }

    @Test
    void getAllTicketsReturnsEmptyList() throws Exception {

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/v1/tickets");
        when(service.getTickets(null, null))
                .thenReturn(Collections.emptyList());

        controller.execute();

        verify(service).getTickets(null, null);

        assertEquals("[]", getResponseBody());
    }

    @Test
    void getTicketById() throws Exception {

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/v1/tickets/1");

        Ticket ticket = new Ticket(
                1L, "Fix login issue", "Robert", "OPEN"
        );

        when(service.getTicket(1L)).thenReturn(ticket);

        controller.execute();

        verify(service).getTicket(1L);
        verify(response).setStatus(200);

        assertTrue(getResponseBody().contains("Fix login issue"));
    }

    @Test
    void getTicketNotFound() throws Exception {

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/v1/tickets/99");

        when(service.getTicket(99L)).thenReturn(null);

        controller.execute();

        verify(response).setStatus(404);

        assertTrue(getResponseBody().contains("Ticket not found"));
    }

    @Test
    void createTicket() throws Exception {

        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/v1/tickets");

        setRequestBody(
                "{\"summary\":\"New ticket\",\"assignee\":\"Robert\",\"status\":\"OPEN\"}"
        );

        Ticket savedTicket = new Ticket(
                10L, "New ticket", "Robert", "OPEN"
        );

        when(service.createTicket(any(Ticket.class)))
                .thenReturn(savedTicket);

        controller.execute();

        verify(service).createTicket(any(Ticket.class));
        verify(response).setStatus(201);

        assertTrue(getResponseBody().contains("New ticket"));
    }

    @Test
    void createTicketWithIdReturnsBadRequest() throws Exception {

        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/v1/tickets/1");

        controller.execute();

        verify(response).setStatus(400);
        verifyNoInteractions(service);

        assertTrue(getResponseBody().contains("ID must not be supplied"));
    }

    @Test
    void updateTicket() throws Exception {

        when(request.getMethod()).thenReturn("PUT");
        when(request.getRequestURI()).thenReturn("/api/v1/tickets/1");

        setRequestBody(
                "{\"summary\":\"Updated summary\",\"assignee\":\"Robert\",\"status\":\"CLOSED\"}"
        );

        when(service.updateTicket(eq(1L), any(Ticket.class)))
                .thenReturn(true);

        controller.execute();

        verify(service).updateTicket(eq(1L), any(Ticket.class));
        verify(response).setStatus(200);

        assertTrue(getResponseBody().contains("Updated summary"));
    }

    @Test
    void updateTicketNotFound() throws Exception {

        when(request.getMethod()).thenReturn("PUT");
        when(request.getRequestURI()).thenReturn("/api/v1/tickets/99");

        setRequestBody(
                "{\"summary\":\"Updated summary\",\"assignee\":\"Robert\",\"status\":\"CLOSED\"}"
        );

        when(service.updateTicket(eq(99L), any(Ticket.class)))
                .thenReturn(false);

        controller.execute();

        verify(response).setStatus(404);

        assertTrue(getResponseBody().contains("Ticket not found"));
    }

    @Test
    void updateWithoutIdReturnsBadRequest() throws Exception {

        when(request.getMethod()).thenReturn("PUT");
        when(request.getRequestURI()).thenReturn("/api/v1/tickets");

        controller.execute();

        verify(response).setStatus(400);
        verifyNoInteractions(service);
    }

    @Test
    void deleteTicket() throws Exception {

        when(request.getMethod()).thenReturn("DELETE");
        when(request.getRequestURI()).thenReturn("/api/v1/tickets/1");

        when(service.deleteTicket(1L)).thenReturn(true);

        controller.execute();

        verify(service).deleteTicket(1L);
        verify(response).setStatus(204);

        assertEquals("", getResponseBody());
    }

    @Test
    void deleteTicketNotFound() throws Exception {

        when(request.getMethod()).thenReturn("DELETE");
        when(request.getRequestURI()).thenReturn("/api/v1/tickets/99");

        when(service.deleteTicket(99L)).thenReturn(false);

        controller.execute();

        verify(response).setStatus(404);

        assertTrue(getResponseBody().contains("Ticket not found"));
    }

    @Test
    void unsupportedMethodReturns405() throws Exception {

        when(request.getMethod()).thenReturn("PATCH");
        when(request.getRequestURI()).thenReturn("/api/v1/tickets");

        controller.execute();

        verify(response).setHeader(
                "Allow",
                "GET, POST, PUT, DELETE"
        );

        verify(response).setStatus(405);

        assertTrue(getResponseBody().contains("Method not allowed"));
    }

    @Test
    void invalidTicketIdReturns400() throws Exception {

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/v1/tickets/abc");

        controller.execute();

        verify(response).setStatus(400);

        assertTrue(getResponseBody().contains("Invalid ticket ID"));
    }
}