package com.example.accidentapp.factory;

import com.example.accidentapp.model.Issue;

/**
 * Abstract Factory interface — decouples incident creation from the UI layer.
 */
public interface AccidentFactory {

    /**
     * Creates a new Issue with the supplied user-facing data.
     * The concrete factory decides the Priority and the location-specific field.
     */
    Issue createIssue(String title, String description);

    /**
     * Returns the emergency contact number appropriate for this context.
     */
    String createEmergencyContact();
}
