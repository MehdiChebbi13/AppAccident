package com.example.accidentapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.accidentapp.R;
import com.example.accidentapp.factory.AccidentFactory;
import com.example.accidentapp.factory.HighwayFactory;
import com.example.accidentapp.factory.UrbanFactory;
import com.example.accidentapp.interfaces.Notifiable;
import com.example.accidentapp.model.Issue;

/**
 * Step-3 form — user selects a context (highway / urban), fills in title
 * and description, then taps "Valider". The fragment delegates object
 * creation entirely to the chosen Abstract Factory and notifies the host
 * Activity via the Notifiable interface.
 */
public class Screen3Fragment extends Fragment {

    public static final String FRAGMENT_ID  = "Screen3Fragment";
    public static final int    REQUEST_CODE = 300;

    private Notifiable notifiable;

    // ── Factory override ─────────────────────────────────────────────────────

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Notifiable) {
            notifiable = (Notifiable) context;
        } else {
            throw new IllegalStateException(
                    context.getClass().getSimpleName()
                    + " must implement Notifiable");
        }
    }

    // ── Lifecycle ────────────────────────────────────────────────────────────

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_screen3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RadioGroup radioGroup     = view.findViewById(R.id.radio_group_context);
        EditText   editTitle      = view.findViewById(R.id.edit_title);
        EditText   editDescription = view.findViewById(R.id.edit_description);
        Button     btnValidate    = view.findViewById(R.id.btn_validate);

        btnValidate.setOnClickListener(v -> {

            // ── 1. Validate inputs ─────────────────────────────────────────
            String title = editTitle.getText().toString().trim();
            String desc  = editDescription.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                editTitle.setError("Le titre est obligatoire");
                editTitle.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(desc)) {
                editDescription.setError("La description est obligatoire");
                editDescription.requestFocus();
                return;
            }

            // ── 2. Select factory based on RadioGroup ──────────────────────
            AccidentFactory factory;
            int checkedId = radioGroup.getCheckedRadioButtonId();
            if (checkedId == R.id.radio_highway) {
                factory = new HighwayFactory();
            } else {
                factory = new UrbanFactory();
            }

            // ── 3. Create issue via factory (Abstract Factory pattern) ──────
            Issue newIssue = factory.createIssue(title, desc);

            // ── 4. Notify host Activity ────────────────────────────────────
            notifiable.onDataChange(
                    FRAGMENT_ID,
                    newIssue,
                    REQUEST_CODE,
                    newIssue.getSafetyProtocol()
            );
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        notifiable = null;
    }
}
