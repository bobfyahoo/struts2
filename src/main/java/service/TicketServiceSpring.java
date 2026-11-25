package service;

import dao.TicketRepository;
import model.Ticket;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TicketServiceSpring {

    private final TicketRepository repository;

    public TicketServiceSpring(TicketRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Ticket> getTickets() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Ticket getTicket(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Ticket not found: " + id
                        )
                );
    }

    public Ticket createTicket(Ticket ticket) {
        ticket.setId(null);
        return repository.save(ticket);
    }

    public Ticket updateTicket(Long id, Ticket updatedTicket) {

        Ticket existing = getTicket(id);

        existing.setSummary(updatedTicket.getSummary());
        existing.setAssignee(updatedTicket.getAssignee());
        existing.setStatus(updatedTicket.getStatus());

        return repository.save(existing);
    }

    public boolean deleteTicket(Long id) {

        if (!repository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Ticket not found: " + id
            );
        }

        repository.deleteById(id);

        return true;
    }
}
