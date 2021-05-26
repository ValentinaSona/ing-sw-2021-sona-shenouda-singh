package it.polimi.ingsw.server.exception;

public class VaticanReportException extends Exception {
    private final int report;

    public VaticanReportException(int message) {
        this.report = message;
    }

    public int getReport() {
        return report;
    }}
