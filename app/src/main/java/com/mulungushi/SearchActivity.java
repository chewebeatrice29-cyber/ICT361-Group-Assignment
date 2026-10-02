package com.mulungushi;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {

    private LinearLayout recentSearchesSection;
    private LinearLayout emptyStateSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> finish());

        recentSearchesSection = findViewById(R.id.recentSearchesSection);
        emptyStateSection = findViewById(R.id.emptyStateSection);

        EditText searchInput = findViewById(R.id.searchInput);
        searchInput.requestFocus();

        TextView chipAll = findViewById(R.id.chipAll);
        TextView chipGroups = findViewById(R.id.chipGroups);
        TextView chipMembers = findViewById(R.id.chipMembers);
        TextView chipMessages = findViewById(R.id.chipMessages);

        chipAll.setOnClickListener(v -> setActiveChip(chipAll, chipGroups, chipMembers, chipMessages));
        chipGroups.setOnClickListener(v -> setActiveChip(chipGroups, chipAll, chipMembers, chipMessages));
        chipMembers.setOnClickListener(v -> setActiveChip(chipMembers, chipAll, chipGroups, chipMessages));
        chipMessages.setOnClickListener(v -> setActiveChip(chipMessages, chipAll, chipGroups, chipMembers));

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty() || s.toString().trim().length() < 2) {
                    recentSearchesSection.setVisibility(View.VISIBLE);
                    emptyStateSection.setVisibility(View.GONE);
                } else {
                    recentSearchesSection.setVisibility(View.GONE);
                    emptyStateSection.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void setActiveChip(TextView active, TextView... others) {
        active.setBackgroundResource(R.drawable.chip_active_background);
        active.setTextColor(0xFFFFFFFF);

        for (TextView chip : others) {
            chip.setBackgroundResource(R.drawable.chip_inactive_background);
            chip.setTextColor(0xFF555555);
        }
    }
}