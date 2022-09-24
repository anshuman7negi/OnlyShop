package app.anshuman.lastshop.Upload;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.Objects;

import app.anshuman.lastshop.Model.ItemModel;
import app.anshuman.lastshop.databinding.ActivityPostBinding;

public class PostActivity extends AppCompatActivity {

    ActivityPostBinding binding;

    Uri imageUri;
    StorageReference storageReference;
    DatabaseReference reference;
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        category = getIntent().getStringExtra("category");

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(view -> {
            finish();
        });

        storageReference = FirebaseStorage.getInstance().getReference().child("Products");
        reference = FirebaseDatabase.getInstance().getReference().child("Products");


        binding.itemImage.setOnClickListener(view -> {
            requestPermissions();
        });

        binding.btnUpload.setOnClickListener(view -> {
            String pName = binding.itemName.getText().toString();
            String pDescription = binding.itemDescription.getText().toString();
            int price = Integer.parseInt(binding.ietmPrice.getText().toString());
            String weight = binding.itemWeight.getText().toString();

            if (pName.isEmpty() || pDescription.isEmpty() || weight.isEmpty() || price == 0){
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
            }else if (price < 1){
                Toast.makeText(this, "Enter valid price for product", Toast.LENGTH_SHORT).show();
            }else {
                uploadProduct(pName,pDescription,price,weight);
            }


        });


    }

    private void uploadProduct(String pName, String pDescription, int price, String weight) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final StorageReference sRef = storageReference.child(System.currentTimeMillis()+".jpg");
        sRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                sRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();

            ItemModel model = new ItemModel();
            String id = reference.push().getKey();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


            model.setImage(imageUrl);
            model.setItemName(pName);
            model.setItemDescription(pDescription);
            model.setItemWeight(weight);
            model.setPrice(price);
            model.setRating(4);
            model.setSeller(user.getUid());
            model.setItemId(id);
            model.setCategory(category);

            assert id != null;
            reference.child(id).setValue(model).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(PostActivity.this, "Product uploaded", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(PostActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }));



    }

    private void requestPermissions() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent,101);
                        }
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                            Toast.makeText(PostActivity.this, "Need storage permissions", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).withErrorListener(error -> Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show())
                .onSameThread().check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101){
            if (resultCode == RESULT_OK){
                imageUri = data.getData();
                binding.itemImage.setImageURI(imageUri);

            }
        }else {
            Toast.makeText(this, "Please select image", Toast.LENGTH_SHORT).show();
        }
    }
}