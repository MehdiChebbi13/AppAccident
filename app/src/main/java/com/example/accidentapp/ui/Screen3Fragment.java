package com.example.accidentapp.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Locale;

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
 *
 * Supporte également la saisie vocale (Exercice 2) pour remplir le titre
 * ou la description via l'app Google de reconnaissance vocale.
 */
public class Screen3Fragment extends Fragment {

    public static final String FRAGMENT_ID  = "Screen3Fragment";
    public static final int    REQUEST_CODE = 300;

    private Notifiable notifiable;

    // ── Saisie vocale ────────────────────────────────────────────────────────

    /** Champ EditText cible courant pour la reconnaissance vocale. */
    private EditText currentTargetEditText;

    /** Launcher pour l'intent ACTION_RECOGNIZE_SPEECH. */
    private ActivityResultLauncher<Intent> voiceLauncher;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enregistrement du launcher de reconnaissance vocale (doit se faire
        // ici, et non dans onViewCreated, pour respecter le cycle de vie de
        // registerForActivityResult).
        voiceLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK
                            && result.getData() != null) {
                        ArrayList<String> matches = result.getData()
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (matches != null && !matches.isEmpty()
                                && currentTargetEditText != null) {
                            // On insère la meilleure correspondance dans le champ ciblé
                            currentTargetEditText.setText(matches.get(0));
                        }
                    }
                });
    }

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

        RadioGroup  radioGroup      = view.findViewById(R.id.radio_group_context);
        EditText    editTitle       = view.findViewById(R.id.edit_title);
        EditText    editDescription = view.findViewById(R.id.edit_description);
        Button      btnValidate     = view.findViewById(R.id.btn_validate);

        // ── Boutons de saisie vocale ───────────────────────────────────────
        ImageButton btnVoiceTitle       = view.findViewById(R.id.btn_voice_title);
        ImageButton btnVoiceDescription = view.findViewById(R.id.btn_voice_description);

        btnVoiceTitle.setOnClickListener(v -> startVoiceRecognition(editTitle));
        btnVoiceDescription.setOnClickListener(v -> startVoiceRecognition(editDescription));

        // ── Validation du formulaire ───────────────────────────────────────
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

    // ── Reconnaissance vocale ────────────────────────────────────────────────

    /**
     * Lance l'intent système de reconnaissance vocale et mémorise le champ
     * EditText à remplir avec le résultat. La permission RECORD_AUDIO est
     * déclarée dans le manifest ; aucune demande à l'exécution n'est
     * nécessaire car l'app Google gère elle-même la capture audio.
     */
    private void startVoiceRecognition(EditText target) {
        currentTargetEditText = target;

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                        "Parlez pour remplir le champ...");

        try {
            voiceLauncher.launch(intent);
        } catch (Exception e) {
            Log.e("Screen3Fragment",
                  "Reconnaissance vocale non supportée sur cet appareil.", e);
            Toast.makeText(getContext(),
                    "Reconnaissance vocale non supportée",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
