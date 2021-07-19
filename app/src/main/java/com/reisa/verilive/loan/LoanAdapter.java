package com.reisa.verilive.loan;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.reisa.verilive.R;
import com.reisa.verilive.loan.dummy.Loan;
import com.reisa.verilive.roomchat.DashboardActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LoanAdapter extends RecyclerView.Adapter<LoanAdapter.ViewHolder> {
    private List<Loan> loanList;

    public LoanAdapter(List<Loan> loanList) {
        this.loanList = loanList;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loan_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Loan loan = loanList.get(position);
        int convertAmount = Integer.valueOf(loan.getAmount());
        holder.textCode.setText("Code : " + loan.getCode());
        holder.textAmount.setText("Amount : Rp" + String.format("%,d", convertAmount));
        holder.textDate.setText("Date : " + loan.getDate());
        holder.textStatus.setText("Status : " + loan.getStatus());

        if (loan.getStatus().contentEquals("Belum terverifikasi")) {
            holder.buttonVerify.setVisibility(View.VISIBLE);
            holder.buttonVerify.setOnClickListener(v -> {
                Intent moveDashboard = new Intent(holder.itemView.getContext(), DashboardActivity.class);
                moveDashboard.putExtra("documentID", loan.getDocumentID());
                holder.itemView.getContext().startActivity(moveDashboard);
            });
        } else {
            holder.buttonVerify.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return loanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textCode, textAmount, textDate, textStatus;
        private Button buttonVerify;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textCode = itemView.findViewById(R.id.text_code);
            textAmount = itemView.findViewById(R.id.text_amount);
            textDate = itemView.findViewById(R.id.text_date);
            textStatus = itemView.findViewById(R.id.text_status);
            buttonVerify = itemView.findViewById(R.id.btnVerify);
        }
    }
}
