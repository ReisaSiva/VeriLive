package com.reisa.verilive.loan;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.reisa.verilive.R;
import com.reisa.verilive.register.model.User;

public class LoanAdapter extends FirestoreRecyclerAdapter<User, LoanAdapter.LoanHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See
     * {@link FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public LoanAdapter(FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(LoanHolder loanHolder, int i, User user) {
        loanHolder.textViewName.setText(user.getPersonalName());
    }

    @NonNull
    @Override
    public LoanHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loan_item, parent, false);
        return new LoanHolder(view);
    }

    class LoanHolder extends RecyclerView.ViewHolder{

        TextView textViewName;

        public LoanHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);

        }
    }
}
