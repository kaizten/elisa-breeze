package com.kaizten.prmp.conversor.realTestMAD;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceInformation {
    private String key;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private List<String> agents = new ArrayList<>();
    private int rowIndex;

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public OffsetDateTime getStartTime() {
        return startTime;}
    
    public void setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;}
    
    public OffsetDateTime getEndTime() {
        return endTime;}
    
    public void setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;}
    
    public List<String> getAgents() {
        return agents;}
    
    public void setAgents(List<String> agents) {
        this.agents = agents;}
    
    public int getRowIndex() {
        return rowIndex;}

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;}

    public int getNeededEmployees() {
        return (agents != null) ? agents.size() : 0;
    }

    public int rowScore() {
        int score = 0;
        if (startTime != null){score += 1;}
        if (endTime != null){score += 1;}
        if (agents != null && !agents.isEmpty()){score += 2;}
        return score;
    }
}
