package com.example.accidentapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private LinearLayout navHome, navReport, navAlerts, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Handle system window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Apply status bar padding to content only
            findViewById(R.id.content_frame).setPadding(
                    systemBars.left, systemBars.top, systemBars.right, 0);

            // Apply navigation bar inset to the system spacer inside bottom nav
            View spacer = findViewById(R.id.nav_system_spacer);
            spacer.getLayoutParams().height = systemBars.bottom;
            spacer.requestLayout();

            return insets;
        });

        // Bind nav items
        navHome    = findViewById(R.id.nav_home);
        navReport  = findViewById(R.id.nav_report);
        navAlerts  = findViewById(R.id.nav_alerts);
        navProfile = findViewById(R.id.nav_profile);

        // Set click listeners
        navHome.setOnClickListener(v -> setActiveTab(0));
        navReport.setOnClickListener(v -> setActiveTab(1));
        navAlerts.setOnClickListener(v -> setActiveTab(2));
        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        // Home is active by default
        setActiveTab(0);
    }

    private void setActiveTab(int index) {
        LinearLayout[] tabs = { navHome, navReport, navAlerts, navProfile };

        for (int i = 0; i < tabs.length; i++) {
            if (i == index) {
                applyActiveStyle(tabs[i]);
            } else {
                applyInactiveStyle(tabs[i]);
            }
        }
    }

    private void applyActiveStyle(LinearLayout tab) {
        tab.setBackgroundResource(R.drawable.nav_item_active_bg);
        ImageView icon = (ImageView) tab.getChildAt(0);
        TextView label = (TextView) tab.getChildAt(1);
        icon.setColorFilter(getColor(R.color.nav_active));
        label.setTextColor(getColor(R.color.nav_active));
    }

    private void applyInactiveStyle(LinearLayout tab) {
        tab.setBackgroundResource(android.R.color.transparent);
        ImageView icon = (ImageView) tab.getChildAt(0);
        TextView label = (TextView) tab.getChildAt(1);
        icon.setColorFilter(getColor(R.color.nav_inactive));
        label.setTextColor(getColor(R.color.nav_inactive));
    }
}
