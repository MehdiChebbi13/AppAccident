package com.example.accidentapp.factory;

import com.example.accidentapp.model.HighwayIssue;
import com.example.accidentapp.model.Issue;

/**
 * Concrete factory for highway incidents.
 * Always produces HighwayIssue (Priority.CRITICAL).
 * Emergency contact: 15 (SAMU / medical rescue on motorways).
 */
public class HighwayFactory implements AccidentFactory {

    @Override
    public Issue createIssue(String title, String description) {
        String id = "HWY-" + System.currentTimeMillis();
        return new HighwayIssue(id, title, description, /* highwayCode */ "A" + (int)(Math.random() * 100));
    }

    @Override
    public String createEmergencyContact() {
        return "15";
    }
}
