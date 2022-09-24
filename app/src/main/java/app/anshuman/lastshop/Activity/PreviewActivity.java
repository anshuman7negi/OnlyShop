package app.anshuman.lastshop.Activity;

import static app.anshuman.lastshop.Adapter.ItemAdapter.list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import app.anshuman.lastshop.BuildConfig;
import app.anshuman.lastshop.Model.ItemModel;
import app.anshuman.lastshop.R;
import app.anshuman.lastshop.databinding.ActivityPreviewBinding;

public class PreviewActivity extends AppCompatActivity {

    ActivityPreviewBinding binding;
    ItemModel model;
    int position;

    DatabaseReference reference;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        position = getIntent().getIntExtra("pos",0);
        model = list.get(position);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(view -> {
            finish();
        });

        reference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        binding.itemName.setText(model.getItemName());
        binding.itemPrice.setText(String.valueOf(model.getPrice()));
        binding.itemWeight.setText(model.getItemWeight());
        Picasso.get().load(model.getImage())
                .placeholder(R.color.backgroundColor)
                .into(binding.itemImage);



//        getShopDetails();
        binding.addToCartLl.setOnClickListener(view -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (binding.addToCartLl.getTag().toString().equals("added")){
                FirebaseDatabase.getInstance().getReference().child("Cart")
                        .child(user.getUid())
                        .child(model.getItemId())
                        .removeValue();
            }else {
                addCart(model);
            }
        });

        getCartCount();
        checkAlreadyItemCart(model.getItemId(),binding.addtocart, binding.cartImg,binding.addToCartLl);

        binding.apvShare.setOnClickListener(view -> {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "eCommerce");
                String shareMessage= "\nLet me recommend you this application\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch(Exception e) {
                //e.toString();
                e.printStackTrace();
            }
        });

        binding.imgCart.setOnClickListener(view ->
                startActivity(new Intent(PreviewActivity.this,CartActivity.class)));
    }
//    private void getShopDetails(){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
//        reference.child(model.getSeller()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()){
//                    String name = snapshot.child("shopName").getValue().toString();
//
//                    if (snapshot.child("shopName").exists()){
//                        if (name.equals("")){
//                            binding.shopName.setText("No shop");
//                        }else {
//                            binding.shopName.setText(name+"'s");
//                        }
//                    }else {
//                        binding.shopName.setText("No shop");
//
//                    }
//
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(PreviewActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void addCart(ItemModel model) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Cart");


        HashMap<String,Object> map = new HashMap<>();
        map.put("image",model.getImage());
        map.put("itemName",model.getItemName());
        map.put(user.getUid(),true);
        map.put("price",model.getPrice());
        map.put("quantity",1);
        map.put("itemWeight",model.getItemWeight());
        map.put("itemId",model.getItemId());
        map.put("total",model.getPrice());
        map.put("sellBy",model.getSeller());

        reference.child(user.getUid()).child(model.getItemId()).setValue(map);

    }

    private void checkAlreadyItemCart(String itemId, TextView txtCart, ImageView imgCart, LinearLayout lyt){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Cart");
        reference.child(user.getUid()).child(itemId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.child(user.getUid()).exists()){
                        lyt.setTag("added");
                        txtCart.setText("Remove Cart");
                        txtCart.setTextColor(getResources().getColor(R.color.colorPrimary));
                        imgCart.setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
                    }else {
                        lyt.setTag("not");
                        txtCart.setText("Add Cart");
                        txtCart.setTextColor(getResources().getColor(R.color.cartColor));
                        imgCart.setColorFilter(getResources().getColor(R.color.cartColor), android.graphics.PorterDuff.Mode.SRC_IN);


                    }
                }else {
                    lyt.setTag("not");

                    txtCart.setText("Add Cart");
                    txtCart.setTextColor(getResources().getColor(R.color.cartColor));
                    imgCart.setColorFilter(getResources().getColor(R.color.cartColor), android.graphics.PorterDuff.Mode.SRC_IN);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PreviewActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getCartCount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Cart");
        assert user != null;
        databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    binding.cartCount.setVisibility(View.VISIBLE);
                    binding.cartCount.setText(String.valueOf(snapshot.getChildrenCount()));

                }else {
                    binding.cartCount.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PreviewActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}