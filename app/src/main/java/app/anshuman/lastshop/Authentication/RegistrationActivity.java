package app.anshuman.lastshop.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import app.anshuman.lastshop.PreferenceManager;
import app.anshuman.lastshop.databinding.ActivityReferenceBinding;
import app.anshuman.lastshop.databinding.ActivityRegistrationBinding;

public class RegistrationActivity extends AppCompatActivity {

    ActivityRegistrationBinding binding;
    String type;

    FirebaseAuth auth;
    DatabaseReference reference;

    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);
        type = getIntent().getStringExtra("type");
        if (type.equals("shop")){
            binding.inputShopName.setVisibility(View.VISIBLE);
            binding.jonTxt.setText("Join as "+"Shop Keeper");
        }else {
            binding.inputShopName.setVisibility(View.GONE);
            binding.jonTxt.setText("Join as "+"Customer");


        }

        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        auth = FirebaseAuth.getInstance();


        binding.btnRegister.setOnClickListener(view -> {
            String name = binding.inputFullName.getText().toString();
            String email = binding.inputEmail.getText().toString();
            String password = binding.inputPassword.getText().toString();
            String shopName = binding.inputShopName.getText().toString();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
            }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Toast.makeText(this, "Enter valid email address", Toast.LENGTH_SHORT).show();
            }else {
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Creating New Account");
                progressDialog.setCancelable(false);
                progressDialog.show();


                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        progressDialog.dismiss();

                        FirebaseUser user = auth.getCurrentUser();
                        assert user !=null;

                        HashMap<String,Object> map = new HashMap<>();
                        map.put("fullName",name);
                        map.put("email",email);
                        map.put("type",type);
                        if (type.equals("shop")){
                            map.put("shopName",shopName);
                        }
                        map.put("uid",user.getUid());

                        reference.child(user.getUid()).setValue(map).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()){
                                progressDialog.dismiss();
                                startActivity(new Intent(this,LoginActivity.class)
                                        .putExtra("type",type)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//                                preferenceManager.putBoolean("isSigned",false);
                                preferenceManager.putBoolean("CisSigned",false);

                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(this, task1.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }else {
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });













            }


        });

        binding.txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}