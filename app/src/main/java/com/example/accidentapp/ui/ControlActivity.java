package com.example.accidentapp.ui;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.accidentapp.R;
import com.example.accidentapp.interfaces.Notifiable;
import com.example.accidentapp.model.Issue;

/**
 * Host Activity for the Abstract Factory demo flow.
 *
 * Starts with Screen3Fragment (form).
 * Implements Notifiable — when Screen3Fragment fires onDataChange, this
 * Activity shows an AlertDialog with the safety protocol, then navigates
 * to Screen1Fragment (detail view) passing the Issue via its Bundle.
 */
public class ControlActivity extends AppCompatActivity implements Notifiable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        // Load Screen3Fragment on first launch only
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new Screen3Fragment())
                    .commit();
        }
    }

    // ── Notifiable ───────────────────────────────────────────────────────────

    /**
     * Receives the newly created Issue from Screen3Fragment, shows the safety
     * protocol in a dialog, then navigates to Screen1Fragment.
     */
    @Override
    public void onDataChange(String fragmentId, Parcelable data,
                             int requestCode, String message) {

        if (!Screen3Fragment.FRAGMENT_ID.equals(fragmentId)
                || requestCode != Screen3Fragment.REQUEST_CODE) {
            return;
        }

        // Safe downcast — data is always an Issue when coming from Screen3Fragment
        Issue issue = (Issue) data;

        // Show safety protocol dialog
        new AlertDialog.Builder(this)
                .setTitle("⚠ Protocole de sécurité")
                .setMessage(message)
                .setPositiveButton("Continuer", (dialog, which) -> {
                    dialog.dismiss();
                    navigateToScreen1(issue);
                })
                .setCancelable(false)
                .show();
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    private void navigateToScreen1(Issue issue) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, Screen1Fragment.newInstance(issue))
                .addToBackStack(null)
                .commit();
    }
}
