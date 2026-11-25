package action.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import model.Ticket;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import service.TicketService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TicketController extends ActionSupport
        implements ModelDriven<Ticket>, ServletRequestAware, ServletResponseAware {

    private HttpServletRequest request;
    private HttpServletResponse response;

    @Setter
    private String id;

    @Setter
    private TicketService service;
    private final ObjectMapper mapper = new ObjectMapper();

    private final Ticket ticket = new Ticket();

    public TicketController() {
        service = new TicketService();
    }

    @Override
    public Ticket getModel() {
        return ticket;
    }

    // GET /api/tickets
    public String index() {
        return handle(() -> {
            String status = request.getParameter("status");
            String assignee = request.getParameter("assignee");

            sendJson(200, service.getTickets(status, assignee));
        });
    }

    // GET /api/tickets/{id}
    public String show() {
        return handle(() -> {
            Ticket ticket = service.getTicket(parseId());

            if (ticket == null) {
                sendError(404, "Ticket not found");
                return;
            }

            sendJson(200, ticket);
        });
    }

    // POST /api/tickets
    public String create() {
        return handle(() -> {
            Ticket created = service.createTicket(ticket);
            sendJson(201, created);
        });
    }

    // PUT /api/tickets/{id}
    public String update() {
        return handle(() -> {
            Long ticketId = parseId();
            boolean updated = service.updateTicket(ticketId, ticket);

            if (!updated) {
                sendError(404, "Ticket not found");
                return;
            }

            ticket.setId(ticketId);
            sendJson(200, ticket);
        });
    }

    // DELETE /api/tickets/{id}
    public String destroy() {
        return handle(() -> {
            boolean deleted = service.deleteTicket(parseId());

            if (!deleted) {
                sendError(404, "Ticket not found");
                return;
            }

            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        });
    }

    // Centralized exception handling
    private String handle(RequestOperation operation) {
        try {
            request.setCharacterEncoding("UTF-8");
            operation.run();

        } catch (IllegalArgumentException e) {
            sendError(400, e.getMessage());

        } catch (IOException e) {
            sendError(400, "Invalid JSON request body");

        } catch (Exception e) {
            log.error("Unexpected error processing ticket request", e);
            sendError(500, "Internal server error");
        }

        // Response has already been written manually.
        return NONE;
    }

    private Long parseId() {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Ticket ID is required");
        }

        try {
            long ticketId = Long.parseLong(id);

            if (ticketId <= 0) {
                throw new IllegalArgumentException(
                        "Ticket ID must be positive"
                );
            }

            return ticketId;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid ticket ID");
        }
    }

    private void sendJson(int status, Object data) {
        try {
            response.setStatus(status);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            mapper.writeValue(response.getOutputStream(), data);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendError(int status, String message) {
        Map<String, Object> error = new HashMap<>();

        error.put("status", status);
        error.put("error", message);

        sendJson(status, error);
    }

    @FunctionalInterface
    private interface RequestOperation {
        void run() throws Exception;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }
}
