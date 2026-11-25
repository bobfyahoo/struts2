package action.v3;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import model.Ticket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@ExtendWith(MockitoExtension.class)
class TicketControllerIntTest {
    private static final String URL = "http://localhost:10000/api/v2/tickets";

    private final ObjectMapper objectMapper = new ObjectMapper();
    RestTemplate restTemplate;

    @BeforeEach
    void setUp() throws Exception {
        restTemplate = new RestTemplate();
    }

    @Test
    void indexReturnsAllTickets() throws Exception {
        ResponseEntity<List<Map<String, Object>>> response = getAllTickets();

        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        log.info("response={}",response.getBody());
    }

    private ResponseEntity<List<Map<String, Object>>> getAllTickets() {
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(RequestEntity.get(URL).build(), new ParameterizedTypeReference<List<Map<String, Object>>>() {
        });
        return response;
    }

    @Test
    void create() throws Exception {
        ResponseEntity<Map<String, Object>> response = createTicket();

        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());
        log.info("response={}",response.getBody());
    }

    private ResponseEntity<Map<String, Object>> createTicket() {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(RequestEntity.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Ticket.builder()
                        .summary("int test"+ UUID.randomUUID().toString())
                        .assignee("Robert")
                        .status("OPEN")
                        .build()), new ParameterizedTypeReference<Map<String, Object>>() {
        });
        return response;
    }

    @Test
    void update() throws Exception {
        ResponseEntity<Map<String, Object>> response = createTicket();

        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());
        log.info("response={}",response.getBody());

        Assertions.assertNotNull(response.getBody());
        String id = response.getBody().get("id").toString();

        ResponseEntity<Map<String, Object>> update = restTemplate.exchange(RequestEntity.put(URL+"/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Ticket.builder()
                        .summary("int test"+ UUID.randomUUID().toString())
                        .assignee("Robert")
                        .status("OPEN")
                        .build()), new ParameterizedTypeReference<Map<String, Object>>() {
        });

        Assertions.assertEquals(HttpStatus.OK.value(), update.getStatusCodeValue());
        log.info("update response={}",update.getBody());
    }

    @Test
    void delete() throws Exception {
        ResponseEntity<Map<String, Object>> response = createTicket();

        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());
        log.info("response={}",response.getBody());

        Assertions.assertNotNull(response.getBody());
        String id = response.getBody().get("id").toString();

        ResponseEntity<Map<String, Object>> update = restTemplate.exchange(RequestEntity.delete(URL+"/"+id).build(),
                new ParameterizedTypeReference<Map<String, Object>>() {});

        Assertions.assertEquals(HttpStatus.OK.value(), update.getStatusCodeValue());
    }
}
