package com.example.accidentapp.factory;

import com.example.accidentapp.model.Issue;
import com.example.accidentapp.model.UrbanIssue;

/**
 * Concrete factory for urban incidents.
 * Always produces UrbanIssue (Priority.MEDIUM).
 * Emergency contact: 17 (Police / urban accident).
 */
public class UrbanFactory implements AccidentFactory {

    @Override
    public Issue createIssue(String title, String description) {
        String id = "URB-" + System.currentTimeMillis();
        return new UrbanIssue(id, title, description, /* streetName */ "Rue inconnue");
    }

    @Override
    public String createEmergencyContact() {
        return "17";
    }
}
