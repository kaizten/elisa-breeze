package com.kaizten.prmp.domain;

import com.kaizten.prmp.domain.problem.Role;

import java.time.Duration;
import java.time.OffsetDateTime;

public class Service implements Comparable<Service> {

    private String code;
    private OffsetDateTime startingTime;
    private OffsetDateTime finishingTime;
    private Role role;
    private int requiredEmployees;

    public String getCode() {
        return this.code;
    }

    public OffsetDateTime getStartingTime() {
        return this.startingTime;
    }

    public OffsetDateTime getFinishingTime() {
        return this.finishingTime;
    }

    public int getRequiredEmployees() {
        return this.requiredEmployees;
    }

    public Role getRole() {
        return this.role;
    }

    public Duration getServiceTime() {
        return Duration.between(this.startingTime, this.finishingTime);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setStartingTime(OffsetDateTime startingTime) {
        this.startingTime = startingTime;
    }

    public void setFinishingTime(OffsetDateTime finishingTime) {
        this.finishingTime = finishingTime;
    }

    public void setRequiredEmployees(int quantity) {
        this.requiredEmployees = quantity;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public int compareTo(Service otherFlight) {
        return this.startingTime.compareTo(otherFlight.getStartingTime());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        final Service otherService = (Service) otherObject;
        return this.startingTime.equals(otherService.getStartingTime());
    }

    @Override
    public int hashCode() {
        return this.startingTime != null ? this.startingTime.hashCode() : 0;
    }

    @Override
    public String toString() {
        return String.format(
                "Service={code=%s, startingTime=%s, finishingTime=%s, serviceTime=%s, role=%s, requiredEmployees=%d}",
                this.code,
                this.startingTime,
                this.finishingTime,
                this.getServiceTime(),
                this.role,
                this.requiredEmployees);
    }
}
