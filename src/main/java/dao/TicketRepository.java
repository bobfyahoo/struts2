package dao;

import model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository
        extends JpaRepository<Ticket, Long> {

    List<Ticket> findByStatus(String status);

    List<Ticket> findByAssignee(String assignee);

    List<Ticket> findByStatusAndAssignee(
            String status,
            String assignee
    );
}