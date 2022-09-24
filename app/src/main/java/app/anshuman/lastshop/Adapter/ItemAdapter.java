package app.anshuman.lastshop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import app.anshuman.lastshop.Activity.PreviewActivity;
import app.anshuman.lastshop.Model.ItemModel;
import app.anshuman.lastshop.R;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    Context context;
    public static ArrayList<ItemModel> list;

    int quantity = 1;
    String updateId;

    public ItemAdapter(Context context, ArrayList<ItemModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemModel model = list.get(position);
        assert model !=null;

        Picasso.get().load(model.getImage())
                .placeholder(R.color.backgroundColor)
                .into(holder.itemImage);
        holder.itemName.setText(model.getItemName());
        holder.itemPrice.setText("₹"+model.getPrice());
        holder.itemWeight.setText(model.getItemWeight());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, PreviewActivity.class);
            intent.putExtra("pos",position);
            context.startActivity(intent);
        });

        holder.btnCart.setOnClickListener(view -> {
            addCart(model);
            holder.btnCart.setVisibility(View.GONE);
            holder.btnRemove.setVisibility(View.VISIBLE);


        });

        holder.btnRemove.setOnClickListener(view -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseDatabase.getInstance().getReference().child("Cart")
                    .child(user.getUid())
                    .child(model.getItemId())
                    .removeValue();
        });


        checkAlreadyItemCart(model.getItemId(), holder.btnRemove, holder.btnCart);


    }



    private void addCart(ItemModel model) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Cart");


        HashMap<String,Object> map = new HashMap<>();
        map.put("image",model.getImage());
        map.put("itemName",model.getItemName());
        map.put(user.getUid(),true);
        map.put("price",model.getPrice());
        map.put("quantity",quantity);
        map.put("itemWeight",model.getItemWeight());
        map.put("itemId",model.getItemId());
        map.put("total",model.getPrice());
        map.put("sellBy",model.getSeller());

        reference.child(user.getUid()).child(model.getItemId()).setValue(map);

    }

    private void checkAlreadyItemCart(String itemId,TextView btnRemove,TextView btnCart){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Cart");
        reference.child(user.getUid()).child(itemId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.child(user.getUid()).exists()){
                        btnRemove.setVisibility(View.VISIBLE);
                        btnCart.setVisibility(View.GONE);
                    }else {
                        btnRemove.setVisibility(View.GONE);
                        btnCart.setVisibility(View.VISIBLE);
                    }
                }else {
                    btnRemove.setVisibility(View.GONE);
                    btnCart.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName,itemPrice,btnCart,itemWeight;
        TextView btnRemove;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            btnCart = itemView.findViewById(R.id.btnToCart);
            itemWeight = itemView.findViewById(R.id.itemWeight);
            btnRemove = itemView.findViewById(R.id.btnRemove);




        }
    }
}
