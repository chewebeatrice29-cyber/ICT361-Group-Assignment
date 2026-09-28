package com.example.labgroupmanager.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.model.Student;

public class ConflictResolverDialog extends DialogFragment {

    public interface ConflictResolutionListener {
        void onKeepLocalProposal(Student localProposal);
        void onAcceptServerRecord(Student serverRecord);
    }

    private final Student localProposal;
    private final Student serverRecord;
    private final ConflictResolutionListener listener;

    public ConflictResolverDialog(Student localProposal, Student serverRecord, ConflictResolutionListener listener) {
        this.localProposal = localProposal;
        this.serverRecord = serverRecord;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_conflict_resolution, null);

        TextView tvLocal = view.findViewById(R.id.tvLocalProposalDetails);
        TextView tvServer = view.findViewById(R.id.tvServerRecordDetails);
        Button btnKeepLocal = view.findViewById(R.id.btnKeepLocal);
        Button btnAcceptServer = view.findViewById(R.id.btnAcceptServer);

        if (localProposal != null) {
            tvLocal.setText("Your Local Proposed Edit:\nName: " + localProposal.getStudentName() +
                    "\nProgramme: " + localProposal.getProgramme() +
                    " | Group: " + localProposal.getLabGroup());
        }

        if (serverRecord != null) {
            tvServer.setText("Current Server Record:\nName: " + serverRecord.getStudentName() +
                    "\nProgramme: " + serverRecord.getProgramme() +
                    " | Group: " + serverRecord.getLabGroup());
        }

        btnKeepLocal.setOnClickListener(v -> {
            if (listener != null) listener.onKeepLocalProposal(localProposal);
            dismiss();
        });

        btnAcceptServer.setOnClickListener(v -> {
            if (listener != null) listener.onAcceptServerRecord(serverRecord);
            dismiss();
        });

        builder.setView(view);
        return builder.create();
    }
}
