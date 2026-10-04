package com.example.labgroupmanager.ui.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.model.SubmissionItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SubmissionAdapter extends RecyclerView.Adapter<SubmissionAdapter.SubmissionViewHolder> {

    private final List<SubmissionItem> submissions;

    public SubmissionAdapter(List<SubmissionItem> submissions) {
        this.submissions = submissions;
    }

    @NonNull
    @Override
    public SubmissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_submission, parent, false);
        return new SubmissionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubmissionViewHolder holder, int position) {
        SubmissionItem sub = submissions.get(position);
        holder.tvAssignmentId.setText(sub.getAssignmentId());
        holder.tvStatus.setText(sub.getStatus());
        holder.tvText.setText(sub.getSubmissionText());
        holder.tvLink.setText(sub.getSubmissionLink());

        String dateStr = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date(sub.getSubmissionDate()));
        holder.tvDate.setText(dateStr);

        if ("GRADED".equalsIgnoreCase(sub.getStatus())) {
            holder.tvGrade.setText("Grade: " + sub.getGradeScore() + " / 100 Marks");
            holder.tvFeedback.setText("Feedback: " + (sub.getFeedback().isEmpty() ? "No feedback provided." : sub.getFeedback()));
            holder.tvStatus.setTextColor(0xFF1E6091); // status synced
        } else {
            holder.tvGrade.setText("Status: Pending Lecturer Review");
            holder.tvFeedback.setText("Feedback: Awaiting grading score.");
            holder.tvStatus.setTextColor(0xFFD97706); // status pending
        }

        holder.tvLink.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sub.getSubmissionLink()));
                v.getContext().startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(v.getContext(), "Opening submission link", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return submissions.size();
    }

    static class SubmissionViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvAssignmentId, tvStatus, tvText, tvLink, tvGrade, tvDate, tvFeedback;

        public SubmissionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAssignmentId = itemView.findViewById(R.id.tvSubItemAssignmentId);
            tvStatus = itemView.findViewById(R.id.tvSubItemStatus);
            tvText = itemView.findViewById(R.id.tvSubItemText);
            tvLink = itemView.findViewById(R.id.tvSubItemLink);
            tvGrade = itemView.findViewById(R.id.tvSubItemGrade);
            tvDate = itemView.findViewById(R.id.tvSubItemDate);
            tvFeedback = itemView.findViewById(R.id.tvSubItemFeedback);
        }
    }
}
