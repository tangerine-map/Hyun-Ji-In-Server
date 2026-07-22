package com.example.hyunjiinserver.core.report.domain;

public enum ReportStatus {

    RECEIVED("접수"),
    REVIEWING("검토"),
    APPLIED("반영"),
    REJECTED("반려");

    private final String description;

    ReportStatus(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
