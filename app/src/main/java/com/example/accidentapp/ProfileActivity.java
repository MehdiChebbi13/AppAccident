package com.example.accidentapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private ListView activityListView;
    private ListView preferenceListView;
    private Spinner filterSpinner;
    private List<ActivityItem> allActivityItems;
    private ActivityAdapter activityAdapter;

    // ─────────────────────────────────────────────────
    //  Data models
    // ─────────────────────────────────────────────────

    static class ActivityItem {
        final String title, subtitle, status, statusKey;
        final int iconResId, iconBgColor, iconColor;

        ActivityItem(String title, String subtitle, String status,
                     int iconResId, int iconBgColor, int iconColor, String statusKey) {
            this.title = title;
            this.subtitle = subtitle;
            this.status = status;
            this.iconResId = iconResId;
            this.iconBgColor = iconBgColor;
            this.iconColor = iconColor;
            this.statusKey = statusKey;
        }
    }

    static class PreferenceItem {
        final String title;
        final int iconResId;
        final boolean isEmergency;

        PreferenceItem(String title, int iconResId, boolean isEmergency) {
            this.title = title;
            this.iconResId = iconResId;
            this.isEmergency = isEmergency;
        }
    }

    // ─────────────────────────────────────────────────
    //  Activity ListView adapter
    // ─────────────────────────────────────────────────

    class ActivityAdapter extends BaseAdapter {
        private List<ActivityItem> items;

        ActivityAdapter(List<ActivityItem> items) {
            this.items = new ArrayList<>(items);
        }

        void updateItems(List<ActivityItem> newItems) {
            items = new ArrayList<>(newItems);
            notifyDataSetChanged();
            // Re-fix height after data changes
            activityListView.post(() -> fixListHeight(activityListView, ActivityAdapter.this));
        }

        @Override public int getCount()             { return items.size(); }
        @Override public Object getItem(int pos)    { return items.get(pos); }
        @Override public long getItemId(int pos)    { return pos; }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ProfileActivity.this)
                        .inflate(R.layout.item_activity_row, parent, false);
            }
            ActivityItem item = items.get(pos);

            FrameLayout iconContainer = convertView.findViewById(R.id.activity_icon_container);
            ImageView icon    = convertView.findViewById(R.id.activity_icon);
            TextView title    = convertView.findViewById(R.id.activity_title);
            TextView subtitle = convertView.findViewById(R.id.activity_subtitle);
            TextView status   = convertView.findViewById(R.id.activity_status);

            iconContainer.setBackgroundTintList(ColorStateList.valueOf(item.iconBgColor));
            icon.setImageResource(item.iconResId);
            icon.setColorFilter(item.iconColor);
            title.setText(item.title);
            subtitle.setText(item.subtitle);
            status.setText(item.status);

            // Status badge color
            int badgeBg = item.statusKey.equals("validated")
                    ? getColor(R.color.color_surface_variant)
                    : getColor(R.color.color_surface_high);
            status.setBackgroundTintList(ColorStateList.valueOf(badgeBg));

            // Alternate row background for visual separation
            int rowBg = (pos % 2 == 0)
                    ? getColor(R.color.color_surface_lowest)
                    : getColor(R.color.color_surface_low);
            convertView.setBackgroundColor(rowBg);

            return convertView;
        }
    }

    // ─────────────────────────────────────────────────
    //  Preference ListView adapter
    // ─────────────────────────────────────────────────

    class PreferenceAdapter extends BaseAdapter {
        private final List<PreferenceItem> items;

        PreferenceAdapter(List<PreferenceItem> items) { this.items = items; }

        @Override public int getCount()             { return items.size(); }
        @Override public Object getItem(int pos)    { return items.get(pos); }
        @Override public long getItemId(int pos)    { return pos; }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ProfileActivity.this)
                        .inflate(R.layout.item_preference_row, parent, false);
            }
            PreferenceItem item = items.get(pos);

            ImageView icon  = convertView.findViewById(R.id.pref_icon);
            TextView  label = convertView.findViewById(R.id.pref_label);

            icon.setImageResource(item.iconResId);
            if (item.isEmergency) {
                icon.setColorFilter(getColor(R.color.color_tertiary));
                label.setTextColor(getColor(R.color.color_tertiary));
            } else {
                icon.setColorFilter(getColor(R.color.color_on_surface_variant));
                label.setTextColor(getColor(R.color.color_on_surface));
            }
            label.setText(item.title);

            // Middle row gets a slightly different background (matches HTML design)
            convertView.setBackgroundColor(pos == 1
                    ? getColor(R.color.color_surface_low)
                    : getColor(R.color.color_surface_lowest));

            return convertView;
        }
    }

    // ─────────────────────────────────────────────────
    //  Lifecycle
    // ─────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        handleInsets();
        setupActivityList();
        setupFilterSpinner();
        setupPreferenceList();
        setupBottomNav();
    }

    // ─────────────────────────────────────────────────
    //  Setup methods
    // ─────────────────────────────────────────────────

    private void handleInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.profile_root), (v, insets) -> {
                    Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

                    // Status bar padding on app bar
                    LinearLayout appBar = findViewById(R.id.profile_app_bar);
                    appBar.setPadding(
                            dpToPx(20),
                            bars.top + dpToPx(12),
                            dpToPx(20),
                            dpToPx(14)
                    );

                    // System navigation bar spacer inside bottom nav
                    View spacer = findViewById(R.id.nav_system_spacer);
                    if (spacer != null) {
                        spacer.getLayoutParams().height = bars.bottom;
                        spacer.requestLayout();
                    }

                    return insets;
                });
    }

    private void setupActivityList() {
        allActivityItems = new ArrayList<>(Arrays.asList(
                new ActivityItem(
                        "Severe Pothole",
                        "A4 Highway, km 42 · 2 days ago",
                        "Validated",
                        R.drawable.ic_warning,
                        getColor(R.color.color_error_container),
                        getColor(R.color.color_error),
                        "validated"
                ),
                new ActivityItem(
                        "Debris on Road",
                        "Route 66 · 5 days ago",
                        "In Review",
                        R.drawable.ic_build,
                        getColor(R.color.color_secondary_fixed),
                        getColor(R.color.color_secondary),
                        "in_review"
                )
        ));

        activityListView = findViewById(R.id.activity_list_view);
        activityAdapter  = new ActivityAdapter(allActivityItems);
        activityListView.setAdapter(activityAdapter);

        // Fix ListView height inside NestedScrollView (two-pass layout)
        activityListView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        activityListView.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                        fixListHeight(activityListView, activityAdapter);
                    }
                });
    }

    private void setupFilterSpinner() {
        filterSpinner = findViewById(R.id.activity_filter_spinner);

        final String[] options = {"All", "Validated", "In Review", "Pending"};

        // Custom adapter to style spinner items
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, options) {

            @Override
            public View getView(int pos, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(pos, convertView, parent);
                view.setTextSize(12f);
                view.setTextColor(getColor(R.color.color_on_surface));
                view.setPadding(dpToPx(10), dpToPx(4), dpToPx(2), dpToPx(4));
                return view;
            }

            @Override
            public View getDropDownView(int pos, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(pos, convertView, parent);
                view.setTextSize(14f);
                view.setTextColor(getColor(R.color.color_on_surface));
                view.setBackgroundColor(getColor(R.color.color_surface_lowest));
                view.setPadding(dpToPx(16), dpToPx(14), dpToPx(16), dpToPx(14));
                return view;
            }
        };
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selected = options[pos];
                List<ActivityItem> filtered = new ArrayList<>();
                for (ActivityItem item : allActivityItems) {
                    if (selected.equals("All")
                            || (selected.equals("Validated") && item.statusKey.equals("validated"))
                            || (selected.equals("In Review") && item.statusKey.equals("in_review"))
                            || (selected.equals("Pending")   && item.statusKey.equals("pending"))) {
                        filtered.add(item);
                    }
                }
                activityAdapter.updateItems(filtered);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void setupPreferenceList() {
        List<PreferenceItem> prefItems = new ArrayList<>(Arrays.asList(
                new PreferenceItem("Notification Preferences", R.drawable.ic_bell,     false),
                new PreferenceItem("Vehicle Info",             R.drawable.ic_car,      false),
                new PreferenceItem("Emergency Contacts",       R.drawable.ic_plus_circle, true)
        ));

        preferenceListView = findViewById(R.id.preference_list_view);
        PreferenceAdapter prefAdapter = new PreferenceAdapter(prefItems);
        preferenceListView.setAdapter(prefAdapter);

        preferenceListView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        preferenceListView.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                        fixListHeight(preferenceListView, prefAdapter);
                    }
                });
    }

    private void setupBottomNav() {
        LinearLayout navHome    = findViewById(R.id.nav_home);
        LinearLayout navReport  = findViewById(R.id.nav_report);
        LinearLayout navAlerts  = findViewById(R.id.nav_alerts);
        LinearLayout navProfile = findViewById(R.id.nav_profile);

        // Reset all tabs, then activate Profile
        for (LinearLayout tab : new LinearLayout[]{navHome, navReport, navAlerts, navProfile}) {
            tab.setBackgroundResource(android.R.color.transparent);
            ((ImageView) tab.getChildAt(0)).setColorFilter(getColor(R.color.nav_inactive));
            ((TextView)  tab.getChildAt(1)).setTextColor(getColor(R.color.nav_inactive));
        }
        navProfile.setBackgroundResource(R.drawable.nav_item_active_bg);
        ((ImageView) navProfile.getChildAt(0)).setColorFilter(getColor(R.color.nav_active));
        ((TextView)  navProfile.getChildAt(1)).setTextColor(getColor(R.color.nav_active));

        // Other tabs return to MainActivity
        View.OnClickListener goHome = v -> {
            startActivity(new Intent(this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
            finish();
        };
        navHome.setOnClickListener(goHome);
        navReport.setOnClickListener(goHome);
        navAlerts.setOnClickListener(goHome);
        navProfile.setOnClickListener(v -> { /* already here */ });
    }

    // ─────────────────────────────────────────────────
    //  Utilities
    // ─────────────────────────────────────────────────

    /**
     * Forces a ListView to wrap its content height so it renders fully
     * inside a NestedScrollView (which gives it unlimited height).
     */
    private static void fixListHeight(ListView listView, ListAdapter adapter) {
        if (adapter == null || adapter.getCount() == 0) return;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View item = adapter.getView(i, null, listView);
            item.measure(
                    View.MeasureSpec.UNSPECIFIED,
                    View.MeasureSpec.UNSPECIFIED
            );
            totalHeight += item.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
