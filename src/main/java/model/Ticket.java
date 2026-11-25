package model;

public class Ticket {

    private Long id;
    private String summary;
    private String assignee;
    private String status;

    public Ticket() {
    }

    public Ticket(Long id, String summary,
                  String assignee, String status) {
        this.id = id;
        this.summary = summary;
        this.assignee = assignee;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}