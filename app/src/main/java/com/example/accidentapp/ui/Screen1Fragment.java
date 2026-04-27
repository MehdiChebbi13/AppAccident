package com.example.accidentapp.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.accidentapp.R;
import com.example.accidentapp.model.Issue;

/**
 * Displays the details of a single Issue that was passed via Bundle arguments.
 * Also shows the safety protocol produced by getSafetyProtocol() — the actual
 * type (HighwayIssue / UrbanIssue) is restored transparently by Parcelable.
 */
public class Screen1Fragment extends Fragment {

    public static final String ARG_ISSUE_KEY = "arg_issue";

    // ── Factory method ───────────────────────────────────────────────────────

    public static Screen1Fragment newInstance(Issue issue) {
        Screen1Fragment fragment = new Screen1Fragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ISSUE_KEY, issue);
        fragment.setArguments(args);
        return fragment;
    }

    // ── Lifecycle ────────────────────────────────────────────────────────────

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_screen1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve Issue from arguments — Parcelable restores the exact subtype
        Issue issue = null;
        if (getArguments() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                issue = getArguments().getParcelable(ARG_ISSUE_KEY, Issue.class);
            } else {
                //noinspection deprecation
                issue = getArguments().getParcelable(ARG_ISSUE_KEY);
            }
        }

        if (issue == null) return;

        // Bind data to views
        ((TextView) view.findViewById(R.id.tv_issue_id))
                .setText(issue.getId());

        ((TextView) view.findViewById(R.id.tv_issue_title))
                .setText(issue.getTitle());

        ((TextView) view.findViewById(R.id.tv_issue_description))
                .setText(issue.getDescription());

        ((TextView) view.findViewById(R.id.tv_issue_priority))
                .setText(issue.getPriority().name());

        ((TextView) view.findViewById(R.id.tv_issue_status))
                .setText(issue.getStatus().name());

        // Safety protocol — polymorphic call on the concrete type
        ((TextView) view.findViewById(R.id.tv_safety_protocol))
                .setText(issue.getSafetyProtocol());
    }
}
