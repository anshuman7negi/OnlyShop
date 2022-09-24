package app.anshuman.lastshop.Activity;


import static app.anshuman.lastshop.Adapter.CartAdapter.grandTotal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import app.anshuman.lastshop.Adapter.CartAdapter;
import app.anshuman.lastshop.Model.Cart;
import app.anshuman.lastshop.databinding.ActivityCartBinding;

public class CartActivity extends AppCompatActivity {

    ActivityCartBinding binding;

    ArrayList<Cart> list;
    CartAdapter adapter;
    int total;
    Cart cart;
    String orderDate,deliveryTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerView.setHasFixedSize(true);


        list = new ArrayList<>();
        adapter = new CartAdapter(this,list);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(view -> {
            finish();
        });


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd‑MMM‑yyyy hh:mm a", Locale.getDefault());
        orderDate = dateFormat.format(new Date());



        getCartItem();

        binding.btnCheck.setOnClickListener(view -> {
//            Toast.makeText(this, cart.getSellBy(), Toast.LENGTH_SHORT).show();

            String text = binding.selectDate.getText().toString();
            if (text.equals("Choose Delivery Date")){
                Toast.makeText(this, "Please select delivery date", Toast.LENGTH_SHORT).show();
            }else {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                String id = String.valueOf(System.currentTimeMillis());
                HashMap<String, Object> map = new HashMap<>();

                map.put("orderId", id);
                map.put("orderBy", user.getUid());
                map.put("itemId", cart.getItemId());
                map.put("orderTime", orderDate);
                map.put("deliveryTime", deliveryTime);
                map.put("price", grandTotal());
                map.put("status", "pending");
                map.put("sellBy",cart.getSellBy());

                reference.child("Orders").child(id).setValue(map).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(CartActivity.this, MyOrdersActivity.class);
                        intent.putExtra("from", "customer");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        binding.selectDate.setOnClickListener(view -> {
            showDateAndTime();
        });



    }

    private void getCartItem(){
        binding.recyclerView.setNestedScrollingEnabled(false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Cart");
        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        cart = dataSnapshot.getValue(Cart.class);
                        if (dataSnapshot.child(user.getUid()).exists()){
                            list.add(cart);
                        }
                    }
                    binding.recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    if (list.size() > 0){
                        binding.emptyCartImg.setVisibility(View.GONE);
                    }else {
                        binding.emptyCartImg.setVisibility(View.VISIBLE);

                    }
                }else {
                    binding.totalPrice.setText("Rs. 0");
                    binding.emptyCartImg.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CartActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showDateAndTime(){

        SwitchDateTimeDialogFragment dateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
                "Delivery Date",
                "OK",
                "Cancel"
        );

        dateTimeDialogFragment.startAtCalendarView();
        dateTimeDialogFragment.set24HoursMode(true);
        dateTimeDialogFragment.setMinimumDateTime(new GregorianCalendar(2015, Calendar.JANUARY, 1).getTime());
        dateTimeDialogFragment.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());
        dateTimeDialogFragment.setDefaultDateTime(new GregorianCalendar(2022, Calendar.APRIL, 13, 6, 0).getTime());

        try {
            dateTimeDialogFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("dd MMMM", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e("TAG", e.getMessage());
        }

        dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                // Date is get on positive button click
                // Do something
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd‑MMM‑yyyy hh:mm a",Locale.getDefault());
                deliveryTime = dateFormat.format(date);
                binding.selectDate.setText(deliveryTime);
            }

            @Override
            public void onNegativeButtonClick(Date date) {
                // Date is get on negative button click
                Toast.makeText(CartActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        dateTimeDialogFragment.show(getSupportFragmentManager(), "Delivery Time");

    }


}