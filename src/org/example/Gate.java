package org.example;

public class Gate {
    private String gateId, terminal, status, assignedFlightId;

    public Gate(String gateId, String terminal, String status, String assignedFlightId) {
        this.gateId = gateId; this.terminal = terminal;
        this.status = status; this.assignedFlightId = assignedFlightId;
    }
    public String getGateId() { return gateId; }
    public String getTerminal() { return terminal; }
    public String getStatus() { return status; }
    public String getAssignedFlightId() { return assignedFlightId; }
    public void setStatus(String status) { this.status = status; }
    public void setAssignedFlightId(String id) { this.assignedFlightId = id; }
}
