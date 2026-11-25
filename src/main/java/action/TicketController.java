package action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.ActionSupport;
import dao.Database;
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
        implements ServletRequestAware, ServletResponseAware {

    private HttpServletRequest request;
    private HttpServletResponse response;

    private final TicketService service = new TicketService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String execute() {

        try {
            request.setCharacterEncoding("UTF-8");

            Database.initialize();

            String method = request.getMethod();
            Long id = extractId();

            switch (method) {

                case "GET":
                    if (id == null) {
                        getTickets();
                    } else {
                        getTicket(id);
                    }
                    break;

                case "POST":
                    if (id != null) {
                        sendError(400, "ID must not be supplied");
                    } else {
                        createTicket();
                    }
                    break;

                case "PUT":
                    if (id == null) {
                        sendError(400, "Ticket ID is required");
                    } else {
                        updateTicket(id);
                    }
                    break;

                case "DELETE":
                    if (id == null) {
                        sendError(400, "Ticket ID is required");
                    } else {
                        deleteTicket(id);
                    }
                    break;

                default:
                    response.setHeader("Allow",
                        "GET, POST, PUT, DELETE");
                    sendError(405, "Method not allowed");
            }

        } catch (IllegalArgumentException e) {

            sendError(400, e.getMessage());

        } catch (IOException e) {

            sendError(400, "Invalid JSON request body");

        } catch (Exception e) {

            log.error("unexpected {}", e.getMessage(),e);
            sendError(500, "Internal server error");
        }

        return NONE;
    }

    private void getTickets() throws Exception {

        String status = request.getParameter("status");
        String assignee = request.getParameter("assignee");

        sendJson(
            200,
            service.getTickets(status, assignee)
        );
    }

    private void getTicket(Long id) throws Exception {

        Ticket ticket = service.getTicket(id);

        if (ticket == null) {
            sendError(404, "Ticket not found");
            return;
        }

        sendJson(200, ticket);
    }

    private void createTicket() throws Exception {

        Ticket ticket = mapper.readValue(
            request.getInputStream(),
            Ticket.class
        );

        Ticket created = service.createTicket(ticket);

        sendJson(201, created);
    }

    private void updateTicket(Long id) throws Exception {

        Ticket ticket = mapper.readValue(
            request.getInputStream(),
            Ticket.class
        );

        boolean updated = service.updateTicket(id, ticket);

        if (!updated) {
            sendError(404, "Ticket not found");
            return;
        }

        ticket.setId(id);

        sendJson(200, ticket);
    }

    private void deleteTicket(Long id) throws Exception {

        boolean deleted = service.deleteTicket(id);

        if (!deleted) {
            sendError(404, "Ticket not found");
            return;
        }

        response.setStatus(204);
    }

    private Long extractId() {

        String path = request.getRequestURI();

        if (path == null) {
            return null;
        }

        String[] parts = path.split("/");

        String last = parts[parts.length - 1];

        if ("tickets".equals(last) || last.isEmpty()) {
            return null;
        }

        try {
            long id = Long.parseLong(last);

            if (id <= 0) {
                throw new IllegalArgumentException(
                    "Ticket ID must be positive"
                );
            }

            return id;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "Invalid ticket ID"
            );
        }
    }

    private void sendJson(int status, Object data) {

        try {
            response.setStatus(status);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            mapper.writeValue(
                response.getOutputStream(),
                data
            );

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

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }
}
