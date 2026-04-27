package com.example.accidentapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Abstract base class for all incident types.
 * Implements Parcelable without a CREATOR — each concrete subclass
 * provides its own CREATOR so Android can reconstruct the exact type.
 */
public abstract class Issue implements Parcelable {

    protected String id;
    protected String title;
    protected String description;
    protected Priority priority;
    protected Status status;

    // ── Standard constructor ─────────────────────────────────────────────────

    public Issue(String id, String title, String description,
                 Priority priority, Status status) {
        this.id          = id;
        this.title       = title;
        this.description = description;
        this.priority    = priority;
        this.status      = status;
    }

    // ── Parcelable read constructor (common fields) ──────────────────────────

    protected Issue(Parcel in) {
        id          = in.readString();
        title       = in.readString();
        description = in.readString();
        priority    = Priority.valueOf(in.readString());
        status      = Status.valueOf(in.readString());
    }

    // ── Parcelable write (common fields — subclass calls super then writes own) ─

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(priority.name());
        dest.writeString(status.name());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // ── Abstract contract ────────────────────────────────────────────────────

    public abstract String getSafetyProtocol();

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getId()          { return id; }
    public String getTitle()       { return title; }
    public String getDescription() { return description; }
    public Priority getPriority()  { return priority; }
    public Status getStatus()      { return status; }
}
