package dao;

import model.Ticket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDao {

    public List<Ticket> findAll(String status, String assignee)
            throws SQLException {

        List<Ticket> tickets = new ArrayList<Ticket>();

        StringBuilder sql = new StringBuilder(
                "SELECT id, summary, assignee, status FROM tickets WHERE 1=1"
        );

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND status = ?");
        }

        if (assignee != null && !assignee.trim().isEmpty()) {
            sql.append(" AND assignee = ?");
        }

        sql.append(" ORDER BY id");

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int index = 1;

            if (status != null && !status.trim().isEmpty()) {
                ps.setString(index++, status);
            }

            if (assignee != null && !assignee.trim().isEmpty()) {
                ps.setString(index++, assignee);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapTicket(rs));
                }
            }
        }

        return tickets;
    }

    public Ticket findById(long id) throws SQLException {

        String sql =
                "SELECT id, summary, assignee, status FROM tickets WHERE id = ?";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapTicket(rs);
                }
            }
        }

        return null;
    }

    public Ticket create(Ticket ticket) throws SQLException {

        String sql =
                "INSERT INTO tickets (summary, assignee, status) " +
                        "VALUES (?, ?, ?)";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, ticket.getSummary());
            ps.setString(2, ticket.getAssignee());
            ps.setString(3, ticket.getStatus());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    ticket.setId(rs.getLong(1));
                }
            }
        }

        return ticket;
    }

    public boolean update(long id, Ticket ticket)
            throws SQLException {

        String sql =
                "UPDATE tickets SET summary = ?, assignee = ?, status = ? " +
                        "WHERE id = ?";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ticket.getSummary());
            ps.setString(2, ticket.getAssignee());
            ps.setString(3, ticket.getStatus());
            ps.setLong(4, id);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(long id) throws SQLException {

        String sql = "DELETE FROM tickets WHERE id = ?";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            return ps.executeUpdate() > 0;
        }
    }

    private Ticket mapTicket(ResultSet rs) throws SQLException {

        return new Ticket(
                rs.getLong("id"),
                rs.getString("summary"),
                rs.getString("assignee"),
                rs.getString("status")
        );
    }
}
