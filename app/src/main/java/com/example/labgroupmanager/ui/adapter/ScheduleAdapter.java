package com.example.labgroupmanager.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.model.ScheduleItem;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    public interface OnDeleteClickListener {
        void onDeleteClick(ScheduleItem item);
    }

    private final List<ScheduleItem> items;
    private final OnDeleteClickListener deleteClickListener;

    public ScheduleAdapter(List<ScheduleItem> items, OnDeleteClickListener deleteClickListener) {
        this.items = items;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        ScheduleItem item = items.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvTime.setText(item.getTime());
        holder.tvLocation.setText(item.getLocation());

        holder.btnDeleteSchedule.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle, tvTime, tvLocation;
        private final ImageButton btnDeleteSchedule;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvScheduleTitle);
            tvTime = itemView.findViewById(R.id.tvScheduleTime);
            tvLocation = itemView.findViewById(R.id.tvScheduleLocation);
            btnDeleteSchedule = itemView.findViewById(R.id.btnDeleteSchedule);
        }
    }
}
