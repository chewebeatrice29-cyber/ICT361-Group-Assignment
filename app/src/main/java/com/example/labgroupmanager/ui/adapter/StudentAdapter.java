package com.example.labgroupmanager.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.model.Student;

public class StudentAdapter extends ListAdapter<Student, StudentAdapter.StudentViewHolder> {

    public interface OnStudentClickListener {
        void onStudentClick(Student student);
    }

    private final OnStudentClickListener clickListener;

    public StudentAdapter(OnStudentClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
    }

    private static final DiffUtil.ItemCallback<Student> DIFF_CALLBACK = new DiffUtil.ItemCallback<Student>() {
        @Override
        public boolean areItemsTheSame(@NonNull Student oldItem, @NonNull Student newItem) {
            return oldItem.getStudentId().equals(newItem.getStudentId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Student oldItem, @NonNull Student newItem) {
            return oldItem.getStudentNumber().equals(newItem.getStudentNumber()) &&
                    oldItem.getStudentName().equals(newItem.getStudentName()) &&
                    oldItem.getProgramme().equals(newItem.getProgramme()) &&
                    oldItem.getLabGroup().equals(newItem.getLabGroup()) &&
                    oldItem.getSyncStatus().equals(newItem.getSyncStatus());
        }
    };

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = getItem(position);
        holder.bind(student, clickListener);
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvItemName;
        private final TextView tvItemNumber;
        private final TextView tvItemProgramme;
        private final TextView tvItemGroupTag;
        private final TextView tvItemSyncBadge;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemNumber = itemView.findViewById(R.id.tvItemNumber);
            tvItemProgramme = itemView.findViewById(R.id.tvItemProgramme);
            tvItemGroupTag = itemView.findViewById(R.id.tvItemGroupTag);
            tvItemSyncBadge = itemView.findViewById(R.id.tvItemSyncBadge);
        }

        public void bind(Student student, OnStudentClickListener listener) {
            tvItemName.setText(student.getStudentName());
            tvItemNumber.setText("# " + student.getStudentNumber());
            tvItemProgramme.setText("Prog: " + student.getProgramme());
            tvItemGroupTag.setText(student.getLabGroup());

            String status = student.getSyncStatus();
            tvItemSyncBadge.setText("Status: " + status);

            if ("SYNCED".equalsIgnoreCase(status)) {
                tvItemSyncBadge.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_synced));
            } else if ("SAVED_LOCALLY".equalsIgnoreCase(status) || "PENDING".equalsIgnoreCase(status)) {
                tvItemSyncBadge.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_pending));
            } else {
                tvItemSyncBadge.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_failed));
            }

            // TalkBack content description for accessibility
            itemView.setContentDescription("Student " + student.getStudentName() + ", Number " + student.getStudentNumber() + ", Programme " + student.getProgramme() + ", Lab Group " + student.getLabGroup() + ", Sync Status " + status);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onStudentClick(student);
                }
            });
        }
    }
}
