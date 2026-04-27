package com.example.accidentapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ReportActivity extends AppCompatActivity {

    // ── Severity state ───────────────────────────────
    private LinearLayout btnLow, btnMedium, btnHigh;
    private View dotLow, dotMedium, dotHigh;
    private TextView labelLow, labelMedium, labelHigh;
    private int selectedSeverity = 1; // 0=Low 1=Medium 2=High (Medium default)

    // Dot colours for each severity level
    private int[] severityColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report);

        severityColors = new int[]{
                Color.parseColor("#60A5FA"),       // Low  – blue-400
                getColor(R.color.color_secondary), // Medium – orange
                getColor(R.color.color_tertiary)   // High   – red
        };

        handleInsets();
        setupAppBar();
        setupDescriptionCounter();
        setupMediaButtons();
        setupSeveritySelector();
        setupSubmitButtons();
    }

    // ─────────────────────────────────────────────────
    //  Insets (status bar → app bar, nav bar → bottom)
    // ─────────────────────────────────────────────────

    private void handleInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.report_root), (v, insets) -> {
                    Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

                    // Push app bar content below the status bar
                    LinearLayout appBar = findViewById(R.id.report_app_bar);
                    appBar.setPadding(
                            dpToPx(8),
                            bars.top + dpToPx(12),
                            dpToPx(16),
                            dpToPx(12)
                    );

                    // Add system nav bar height to bottom action bar
                    LinearLayout bottomBar = findViewById(R.id.report_bottom_action);
                    bottomBar.setPadding(
                            dpToPx(16),
                            dpToPx(16),
                            dpToPx(16),
                            bars.bottom + dpToPx(16)
                    );

                    return insets;
                });
    }

    // ─────────────────────────────────────────────────
    //  App Bar
    // ─────────────────────────────────────────────────

    private void setupAppBar() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_top_submit).setOnClickListener(v -> submitReport());
    }

    // ─────────────────────────────────────────────────
    //  Description character counter
    // ─────────────────────────────────────────────────

    private void setupDescriptionCounter() {
        EditText editText   = findViewById(R.id.description_edit);
        TextView counter    = findViewById(R.id.char_counter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                int len = s.length();
                counter.setText(len + "/200");
                // Warn visually when approaching limit
                counter.setTextColor(len >= 180
                        ? getColor(R.color.color_tertiary)
                        : getColor(R.color.color_outline));
            }
        });
    }

    // ─────────────────────────────────────────────────
    //  Photo / Video placeholders
    // ─────────────────────────────────────────────────

    private void setupMediaButtons() {
        findViewById(R.id.btn_add_photo).setOnClickListener(v ->
                Toast.makeText(this, "Camera feature coming soon", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btn_add_video).setOnClickListener(v ->
                Toast.makeText(this, "Video feature coming soon", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btn_edit_location).setOnClickListener(v ->
                Toast.makeText(this, "Location editing coming soon", Toast.LENGTH_SHORT).show());
    }

    // ─────────────────────────────────────────────────
    //  Severity selector
    // ─────────────────────────────────────────────────

    private void setupSeveritySelector() {
        btnLow    = findViewById(R.id.btn_severity_low);
        btnMedium = findViewById(R.id.btn_severity_medium);
        btnHigh   = findViewById(R.id.btn_severity_high);

        dotLow    = btnLow.getChildAt(0);
        dotMedium = btnMedium.getChildAt(0);
        dotHigh   = btnHigh.getChildAt(0);

        labelLow    = (TextView) btnLow.getChildAt(1);
        labelMedium = (TextView) btnMedium.getChildAt(1);
        labelHigh   = (TextView) btnHigh.getChildAt(1);

        btnLow.setOnClickListener(v    -> selectSeverity(0));
        btnMedium.setOnClickListener(v -> selectSeverity(1));
        btnHigh.setOnClickListener(v   -> selectSeverity(2));

        // Apply default selection
        selectSeverity(selectedSeverity);
    }

    /**
     * Applies selected/unselected visual style to all three severity buttons.
     * Selected: 2dp coloured border + 8% tint background + coloured text.
     * Unselected: 1dp outline-variant border + white background + muted text.
     */
    private void selectSeverity(int idx) {
        selectedSeverity = idx;

        LinearLayout[] btns = { btnLow, btnMedium, btnHigh };
        View[]         dots = { dotLow, dotMedium, dotHigh };
        TextView[]     lbls = { labelLow, labelMedium, labelHigh };

        for (int i = 0; i < 3; i++) {
            // Always tint the dot to its severity colour
            dots[i].setBackgroundTintList(ColorStateList.valueOf(severityColors[i]));

            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(dpToPx(12));

            if (i == idx) {
                // Selected appearance
                int c = severityColors[i];
                // Fill: severity colour at ~8% opacity
                bg.setColor(Color.argb(20, Color.red(c), Color.green(c), Color.blue(c)));
                bg.setStroke(dpToPx(2), c);
                lbls[i].setTextColor(c);
            } else {
                // Unselected appearance
                bg.setColor(getColor(R.color.color_surface_lowest));
                bg.setStroke(dpToPx(1), getColor(R.color.color_outline_variant));
                lbls[i].setTextColor(getColor(R.color.color_on_surface_variant));
            }
            btns[i].setBackground(bg);
        }
    }

    // ─────────────────────────────────────────────────
    //  Submit action
    // ─────────────────────────────────────────────────

    private void setupSubmitButtons() {
        findViewById(R.id.btn_confirm_report).setOnClickListener(v -> submitReport());
    }

    private void submitReport() {
        String[] severityLabels = { "Low", "Medium", "High" };
        String msg = "Report submitted! Severity: " + severityLabels[selectedSeverity];
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        finish();
    }

    // ─────────────────────────────────────────────────
    //  Utility
    // ─────────────────────────────────────────────────

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
