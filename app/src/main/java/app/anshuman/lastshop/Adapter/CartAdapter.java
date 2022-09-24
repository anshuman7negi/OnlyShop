package app.anshuman.lastshop.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import app.anshuman.lastshop.Model.Cart;
import app.anshuman.lastshop.R;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    Context context;
    static ArrayList<Cart> list;

    int q = 1;
    int _subtotal;

    public CartAdapter(Context context, ArrayList<Cart> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_cart,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Cart cart = list.get(position);

        Picasso.get().load(cart.getImage())
                .placeholder(R.color.backgroundColor)
                .into(holder.itemImage);
        holder.itemName.setText(cart.getItemName());
        holder.itemWeight.setText(cart.getItemWeight());

        holder.quantity.setText(String.valueOf(cart.getQuantity()));
        holder.price.setText(String.valueOf(cart.getPrice()));

        _subtotal = cart.getQuantity() * cart.getPrice();
       holder.total.setText(String.valueOf(_subtotal));

       holder.btnPlus.setOnClickListener(view -> {
           int quantity = Integer.parseInt(holder.quantity.getText().toString());
           if (quantity >=1){
               q+=1;
               updateQuantity(q,cart);
           }

       });
       holder.btnMinus.setOnClickListener(view -> {
           int quantity = Integer.parseInt(holder.quantity.getText().toString());

           if (quantity >=1){
               q-=1;
               updateQuantity(q,cart);

           } else {
               FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
               FirebaseDatabase.getInstance().getReference().child("Cart")
                       .child(user.getUid())
                       .child(cart.getItemId())
                       .removeValue();
           }



       });


       updateTotal(cart);

       TextView text =  ((Activity)context).findViewById(R.id.total_price);
       text.setText("Rs. "+grandTotal());

       holder.deleteCart.setOnClickListener(view -> {
           FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
           FirebaseDatabase.getInstance().getReference().child("Cart")
                   .child(user.getUid())
                   .child(cart.getItemId())
                   .removeValue();
       });


    }



    private void updateQuantity(int v, Cart cart){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Cart")
                .child(user.getUid());
        HashMap<String,Object> map = new HashMap<>();
        map.put("quantity",v);

        reference.child(cart.getItemId()).updateChildren(map);
    }

    private void updateTotal(Cart cart){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Cart")
                .child(user.getUid());
        HashMap<String,Object> map = new HashMap<>();
        map.put("total",_subtotal);
        reference.child(cart.getItemId()).updateChildren(map);
    }

    public static int grandTotal(){

        int totalPrice = 0;
        for(int i = 0 ; i < list.size(); i++) {
            totalPrice += list.get(i).getTotal();
        }

        return totalPrice;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage,btnMinus,btnPlus;
        TextView quantity,price,total;
        TextView itemName,itemWeight;
        ImageView deleteCart;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            btnMinus = itemView.findViewById(R.id.quantity_minus);
            btnPlus = itemView.findViewById(R.id.quantity_plus);
            quantity = itemView.findViewById(R.id.quantity);
            price = itemView.findViewById(R.id.itemPrice);
            total = itemView.findViewById(R.id.sub_total);
            itemName = itemView.findViewById(R.id.itemName);
            itemWeight = itemView.findViewById(R.id.itemWeight);
            deleteCart = itemView.findViewById(R.id.cart_delete);

        }
    }
}
