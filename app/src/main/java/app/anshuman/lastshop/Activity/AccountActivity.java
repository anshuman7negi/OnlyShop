package app.anshuman.lastshop.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import app.anshuman.lastshop.databinding.ActivityAccountBinding;

public class AccountActivity extends AppCompatActivity {

    ActivityAccountBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(view -> {
            finish();
        });
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name = snapshot.child("fullName").getValue().toString();
                    String email = snapshot.child("email").getValue().toString();

                    binding.fullName.setText(name);
                    binding.email.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AccountActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });





    }
}