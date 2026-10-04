package com.example.labgroupmanager.ui.adapter;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.model.MessageItem;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final List<MessageItem> messages;
    private final String currentUserId;

    public ChatAdapter(List<MessageItem> messages, String currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId != null ? currentUserId : "";
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_bubble, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        MessageItem msg = messages.get(position);
        boolean isSentByMe = currentUserId.equals(msg.getSenderId());

        holder.tvSender.setText(msg.getSenderName() + " (" + msg.getSenderRole() + ")");
        holder.tvContent.setText(msg.getContent());

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        holder.tvTime.setText(sdf.format(new Date(msg.getTimestamp())));

        if (isSentByMe) {
            holder.container.setGravity(Gravity.END);
            holder.cardBubble.setCardBackgroundColor(Color.parseColor("#1565C0"));
            holder.tvContent.setTextColor(Color.WHITE);
            holder.tvSender.setTextColor(Color.parseColor("#E3F2FD"));
        } else {
            holder.container.setGravity(Gravity.START);
            holder.cardBubble.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.tvContent.setTextColor(Color.parseColor("#0D233A"));
            holder.tvSender.setTextColor(Color.parseColor("#1565C0"));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout container;
        private final MaterialCardView cardBubble;
        private final TextView tvSender, tvContent, tvTime;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.chatBubbleContainer);
            cardBubble = itemView.findViewById(R.id.cardChatBubble);
            tvSender = itemView.findViewById(R.id.tvChatSender);
            tvContent = itemView.findViewById(R.id.tvChatContent);
            tvTime = itemView.findViewById(R.id.tvChatTime);
        }
    }
}
