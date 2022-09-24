package app.anshuman.lastshop.Upload;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import app.anshuman.lastshop.Activity.AccountActivity;
import app.anshuman.lastshop.Activity.MyOrdersActivity;
import app.anshuman.lastshop.Authentication.LoginActivity;
import app.anshuman.lastshop.R;
import app.anshuman.lastshop.databinding.ActivityAdminBinding;

public class AdminActivity extends AppCompatActivity {

    ActivityAdminBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


//        binding.fruitsLyt.setOnClickListener(view -> {
//            startActivity(new Intent(AdminActivity.this,PostActivity.class)
//                    .putExtra("category","fruits"));
//        });
        binding.vegitableLyt.setOnClickListener(view -> {
            startActivity(new Intent(AdminActivity.this,PostActivity.class)
                    .putExtra("category","vegetables"));
        });
        binding.milkLyt.setOnClickListener(view -> {
            startActivity(new Intent(AdminActivity.this,PostActivity.class)
                    .putExtra("category","dairy"));
        });
        binding.oilLyt.setOnClickListener(view -> {
            startActivity(new Intent(AdminActivity.this,PostActivity.class)
                    .putExtra("category","oil"));
        });
        binding.riceLyt.setOnClickListener(view -> {
            startActivity(new Intent(AdminActivity.this,PostActivity.class)
                    .putExtra("category","grains"));
        });
        binding.detergentLyt.setOnClickListener(view -> {
            startActivity(new Intent(AdminActivity.this,PostActivity.class)
                    .putExtra("category","detergents"));
        });

        binding.txtOrders.setOnClickListener(view -> {
            Intent intent = new Intent(AdminActivity.this, MyOrdersActivity.class);
            intent.putExtra("from","shop");
            startActivity(intent);
        });

        binding.txtSignOut.setOnClickListener(view -> {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            Intent intent1 = new Intent(AdminActivity.this, LoginActivity.class);
                            intent1.putExtra("type","shop");
                            startActivity(intent1);
                            finish();
                        }
                    });
        });

        binding.txtAccount.setOnClickListener(view -> {
            Intent intent = new Intent(AdminActivity.this, AccountActivity.class);

            startActivity(intent);
        });

    }
}