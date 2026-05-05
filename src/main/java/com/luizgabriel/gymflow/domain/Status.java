package com.luizgabriel.gymflow.domain;

import lombok.Getter;

@Getter
public enum Status {
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String status;

    Status(String status) {
        this.status = status;
    }

}
