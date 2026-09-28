package com.example.labgroupmanager.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.model.NotificationItem;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotifViewHolder> {

    private final List<NotificationItem> items;

    public NotificationAdapter(List<NotificationItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotifViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifViewHolder holder, int position) {
        NotificationItem item = items.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvBody.setText(item.getBody());
        holder.tvTime.setText(item.getTime());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class NotifViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle, tvBody, tvTime;

        public NotifViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotifTitle);
            tvBody = itemView.findViewById(R.id.tvNotifBody);
            tvTime = itemView.findViewById(R.id.tvNotifTime);
        }
    }
}
