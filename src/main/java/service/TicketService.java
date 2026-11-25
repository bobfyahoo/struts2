package service;

import dao.TicketDao;
import model.Ticket;

import java.sql.SQLException;
import java.util.List;

public class TicketService {

    private final TicketDao dao = new TicketDao();

    public List<Ticket> getTickets(String status, String assignee)
            throws SQLException {

        return dao.findAll(status, assignee);
    }

    public Ticket getTicket(long id) throws SQLException {

        return dao.findById(id);
    }

    public Ticket createTicket(Ticket ticket)
            throws SQLException {

        validate(ticket);

        ticket.setId(null);

        return dao.create(ticket);
    }

    public boolean updateTicket(long id, Ticket ticket)
            throws SQLException {

        validate(ticket);

        return dao.update(id, ticket);
    }

    public boolean deleteTicket(long id)
            throws SQLException {

        return dao.delete(id);
    }

    private void validate(Ticket ticket) {

        if (ticket == null) {
            throw new IllegalArgumentException(
                    "Ticket body is required"
            );
        }

        if (ticket.getSummary() == null ||
                ticket.getSummary().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Summary is required"
            );
        }

        if (ticket.getSummary().length() > 250) {
            throw new IllegalArgumentException(
                    "Summary cannot exceed 250 characters"
            );
        }

        if (ticket.getAssignee() != null &&
                ticket.getAssignee().length() > 20) {

            throw new IllegalArgumentException(
                    "Assignee cannot exceed 20 characters"
            );
        }

        if (ticket.getStatus() == null ||
                ticket.getStatus().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Status is required"
            );
        }

        if (ticket.getStatus().length() > 20) {
            throw new IllegalArgumentException(
                    "Status cannot exceed 20 characters"
            );
        }
    }
}
