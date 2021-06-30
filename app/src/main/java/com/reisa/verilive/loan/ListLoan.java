package com.reisa.verilive.loan;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reisa.verilive.R;
import com.reisa.verilive.loan.dummy.Loan;

import java.util.ArrayList;
import java.util.List;

public class ListLoan extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private List<Loan> loanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_loan);
        recyclerView = findViewById(R.id.recycler_view);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        String getCurrentUser = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore.collection("Users").document(getCurrentUser).collection("Loan Information").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loanList = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    if (documentSnapshot != null) {
                        Loan loan = new Loan();
                        loan.setAmount(documentSnapshot.getString("amount"));
                        loan.setCode(documentSnapshot.getString("code"));
                        loan.setDate(documentSnapshot.getString("date"));
                        loan.setName(documentSnapshot.getString("name"));
                        loan.setObjective(documentSnapshot.getString("objective"));
                        loan.setStatus(documentSnapshot.getString("status"));

                        loanList.add(loan);
                    }
                }

                LoanAdapter adapter = new LoanAdapter(loanList);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(ListLoan.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

