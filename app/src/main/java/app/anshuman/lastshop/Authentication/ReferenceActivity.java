package app.anshuman.lastshop.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import app.anshuman.lastshop.MainActivity;
import app.anshuman.lastshop.Upload.AdminActivity;
import app.anshuman.lastshop.databinding.ActivityReferenceBinding;

public class ReferenceActivity extends AppCompatActivity {

    ActivityReferenceBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReferenceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        binding.btnCustomer.setOnClickListener(view -> {
            Intent intent;
            if (user !=null){

                intent = new Intent(ReferenceActivity.this, MainActivity.class);

            }else {

                intent = new Intent(ReferenceActivity.this, LoginActivity.class);

            }
            intent.putExtra("type","customer");
            startActivity(intent);

        });
        binding.btnShop.setOnClickListener(view -> {

            Intent intent;
            if (user !=null){

                intent = new Intent(ReferenceActivity.this, AdminActivity.class);

            }else {

                intent = new Intent(ReferenceActivity.this, LoginActivity.class);

            }
            intent.putExtra("type","shop");
            startActivity(intent);

        });

    }
}