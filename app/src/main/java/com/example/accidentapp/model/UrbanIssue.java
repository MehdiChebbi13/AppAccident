package com.example.accidentapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Concrete Issue for urban incidents.
 * Defaults to Priority.MEDIUM.
 */
public class UrbanIssue extends Issue {

    private String streetName;

    // ── Constructor ──────────────────────────────────────────────────────────

    public UrbanIssue(String id, String title, String description, String streetName) {
        super(id, title, description, Priority.MEDIUM, Status.PENDING);
        this.streetName = streetName;
    }

    // ── Parcelable ───────────────────────────────────────────────────────────

    protected UrbanIssue(Parcel in) {
        super(in);
        streetName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(streetName);
    }

    public static final Parcelable.Creator<UrbanIssue> CREATOR =
            new Parcelable.Creator<UrbanIssue>() {
                @Override
                public UrbanIssue createFromParcel(Parcel in) {
                    return new UrbanIssue(in);
                }

                @Override
                public UrbanIssue[] newArray(int size) {
                    return new UrbanIssue[size];
                }
            };

    // ── Abstract implementation ──────────────────────────────────────────────

    @Override
    public String getSafetyProtocol() {
        return "Déplacez les véhicules sur le bas-côté si possible "
             + "et sécurisez le périmètre.";
    }

    // ── Getter ───────────────────────────────────────────────────────────────

    public String getStreetName() { return streetName; }
}
