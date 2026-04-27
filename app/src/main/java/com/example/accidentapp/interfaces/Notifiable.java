package com.example.accidentapp.interfaces;

import android.os.Parcelable;

/**
 * Callback contract between Fragments and their host Activity.
 * The Activity implements this interface; fragments obtain it via
 * (Notifiable) getActivity().
 */
public interface Notifiable {

    /**
     * Called when a Fragment has data ready to hand off to the Activity.
     *
     * @param fragmentId  Identifier of the emitting fragment (e.g. Screen3Fragment.FRAGMENT_ID)
     * @param data        The Parcelable payload (an Issue subclass, etc.)
     * @param requestCode Arbitrary code so the Activity knows how to route the data
     * @param message     Human-readable message (e.g. the safety protocol string)
     */
    void onDataChange(String fragmentId, Parcelable data, int requestCode, String message);
}
