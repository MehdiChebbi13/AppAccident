package com.example.accidentapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Concrete Issue for highway incidents.
 * Always forced to Priority.CRITICAL.
 */
public class HighwayIssue extends Issue {

    private String highwayCode;

    // ── Constructor ──────────────────────────────────────────────────────────

    public HighwayIssue(String id, String title, String description, String highwayCode) {
        super(id, title, description, Priority.CRITICAL, Status.PENDING);
        this.highwayCode = highwayCode;
    }

    // ── Parcelable ───────────────────────────────────────────────────────────

    protected HighwayIssue(Parcel in) {
        super(in);
        highwayCode = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(highwayCode);
    }

    public static final Parcelable.Creator<HighwayIssue> CREATOR =
            new Parcelable.Creator<HighwayIssue>() {
                @Override
                public HighwayIssue createFromParcel(Parcel in) {
                    return new HighwayIssue(in);
                }

                @Override
                public HighwayIssue[] newArray(int size) {
                    return new HighwayIssue[size];
                }
            };

    // ── Abstract implementation ──────────────────────────────────────────────

    @Override
    public String getSafetyProtocol() {
        return "Attention danger extrême : Mettez votre gilet jaune "
             + "et passez derrière la glissière de sécurité.";
    }

    // ── Getter ───────────────────────────────────────────────────────────────

    public String getHighwayCode() { return highwayCode; }
}
