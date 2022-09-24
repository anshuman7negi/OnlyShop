package app.anshuman.lastshop.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import app.anshuman.lastshop.Adapter.ItemAdapter;
import app.anshuman.lastshop.Model.ItemModel;
import app.anshuman.lastshop.databinding.ActivityProductsBinding;

public class ProductsActivity extends AppCompatActivity {

    ActivityProductsBinding binding;
    DatabaseReference reference;
    ArrayList<ItemModel> list = new ArrayList<>();
    ItemAdapter adapter;

    String category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        category = getIntent().getStringExtra("cat");
        reference = FirebaseDatabase.getInstance().getReference().child("Products");

        getData();


    }
    private void getData(){
        binding.recyclerView.setHasFixedSize(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        ItemModel model = dataSnapshot.getValue(ItemModel.class);
                        if (model.getCategory().equals(category)){
                            list.add(model);
                        }
                    }

                    adapter = new ItemAdapter(ProductsActivity.this,list);
                    binding.recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}