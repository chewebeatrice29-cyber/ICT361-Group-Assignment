package com.example.labgroupmanager.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.model.TimetableSlot;

import java.util.List;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.TimetableViewHolder> {

    private final List<TimetableSlot> slots;

    public TimetableAdapter(List<TimetableSlot> slots) {
        this.slots = slots;
    }

    @NonNull
    @Override
    public TimetableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timetable_slot, parent, false);
        return new TimetableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimetableViewHolder holder, int position) {
        TimetableSlot slot = slots.get(position);
        holder.tvTime.setText("Slot " + slot.getSlotNumber() + ": " + slot.getTimeRange());
        holder.tvCode.setText(slot.getCourseCode());
        holder.tvName.setText(slot.getCourseName());
        holder.tvVenue.setText("📍 " + slot.getVenue() + "  •  Lecturer: " + slot.getLecturerName());
    }

    @Override
    public int getItemCount() {
        return slots.size();
    }

    static class TimetableViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTime, tvCode, tvName, tvVenue;

        public TimetableViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTtSlotTime);
            tvCode = itemView.findViewById(R.id.tvTtSlotCode);
            tvName = itemView.findViewById(R.id.tvTtSlotCourseName);
            tvVenue = itemView.findViewById(R.id.tvTtSlotVenue);
        }
    }
}
