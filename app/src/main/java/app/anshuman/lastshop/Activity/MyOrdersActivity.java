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

import java.util.ArrayList;

import app.anshuman.lastshop.Adapter.MyOrderAdapter;
import app.anshuman.lastshop.Model.MyOrders;
import app.anshuman.lastshop.databinding.ActivityMyOrdersBinding;

public class MyOrdersActivity extends AppCompatActivity {

    ActivityMyOrdersBinding binding;
    ArrayList<MyOrders> list = new ArrayList<>();
    MyOrderAdapter adapter;

    FirebaseUser user;

    String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        from = getIntent().getStringExtra("from");

        binding.recyclerView.setHasFixedSize(true);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(view -> {
            finish();
        });

        if (from.equals("customer")){
            getSupportActionBar().setTitle("My Orders");
            getData();
        }else if (from.equals("shop")){
            getSupportActionBar().setTitle("Orders");
            getShopData();
        }

    }
    private void getData(){
        user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Orders");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        MyOrders orders = dataSnapshot.getValue(MyOrders.class);
                        assert orders != null;
                        if (orders.getOrderBy().equals(user.getUid())) {
                            list.add(orders);
                        }
                    }

                    adapter = new MyOrderAdapter(MyOrdersActivity.this,list,from);
                    binding.recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyOrdersActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getShopData(){
        user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Orders");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        MyOrders orders = dataSnapshot.getValue(MyOrders.class);
                        assert orders != null;
                        if (orders.getSellBy().equals(user.getUid())) {
                            list.add(orders);
                        }
                    }


                    adapter = new MyOrderAdapter(MyOrdersActivity.this,list,from);
                    binding.recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyOrdersActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}