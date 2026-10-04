package com.example.labgroupmanager.ui.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.model.ModuleItem;

import java.util.List;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder> {

    private final List<ModuleItem> modules;

    public ModuleAdapter(List<ModuleItem> modules) {
        this.modules = modules;
    }

    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_module_card, parent, false);
        return new ModuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {
        ModuleItem module = modules.get(position);
        holder.tvTitle.setText(module.getModuleTitle());
        holder.tvCourseCode.setText(module.getCourseCode());
        holder.tvDesc.setText(module.getDescription());

        holder.btnDownloadPdf.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(module.getFileUrl()));
                v.getContext().startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(v.getContext(), "Downloading module PDF for " + module.getModuleTitle(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return modules.size();
    }

    static class ModuleViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle, tvCourseCode, tvDesc;
        private final Button btnDownloadPdf;

        public ModuleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvModuleCardTitle);
            tvCourseCode = itemView.findViewById(R.id.tvModuleCardCourseCode);
            tvDesc = itemView.findViewById(R.id.tvModuleCardDesc);
            btnDownloadPdf = itemView.findViewById(R.id.btnDownloadModulePdf);
        }
    }
}
