package app.anshuman.lastshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import app.anshuman.lastshop.Activity.AccountActivity;
import app.anshuman.lastshop.Activity.CartActivity;
import app.anshuman.lastshop.Activity.MyOrdersActivity;
import app.anshuman.lastshop.Activity.ProductsActivity;
import app.anshuman.lastshop.Adapter.HomeSliderAdapter;
import app.anshuman.lastshop.Adapter.ItemAdapter;
import app.anshuman.lastshop.Authentication.LoginActivity;
import app.anshuman.lastshop.Model.ItemModel;
import app.anshuman.lastshop.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActivityMainBinding binding;
    private int dotscount;
    private ImageView[] dots;
    private Integer[] images = {R.drawable.slider1, R.drawable.slider2, R.drawable.slider3};
    Timer timer;
    int page_position = 0;

    DatabaseReference reference;
    ArrayList<ItemModel> list = new ArrayList<>();
    ItemAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reference = FirebaseDatabase.getInstance().getReference().child("Products");

        HomeSliderAdapter viewPagerAdapter = new HomeSliderAdapter(this, images);

        binding.viewPager.setAdapter(viewPagerAdapter);
        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for (int i = 0; i < dotscount; i++) {

            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.non_active_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            binding.SliderDots.addView(dots[i], params);

        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_dot));

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < dotscount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.non_active_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        scheduleSlider();
        getData();


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLyt, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLyt.setDrawerListener(toggle);
        toggle.syncState();


        binding.navigationView.setNavigationItemSelectedListener(this);
        View hView = binding.navigationView.getHeaderView(0);

        binding.toolbar.setNavigationOnClickListener(view ->
                binding.drawerLyt.openDrawer(GravityCompat.START));

        clicks();

        binding.imgCart.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this,CartActivity.class)));

        getUserData(hView);
        getCartCount();

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
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData(){
        binding.recyclerView.setNestedScrollingEnabled(false);
        binding.recyclerView.setHasFixedSize(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        ItemModel model = dataSnapshot.getValue(ItemModel.class);
                        list.add(model);
                    }

                    Collections.reverse(list);
                    adapter = new ItemAdapter(MainActivity.this,list);
                    binding.recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clicks(){
//        binding.fruitsLyt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this, ProductsActivity.class)
//                        .putExtra("cat","fruits"));
//            }
//        });
        binding.vegitableLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProductsActivity.class)
                        .putExtra("cat","vegetables"));
            }
        });
        binding.milkLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProductsActivity.class)
                        .putExtra("cat","dairy"));
            }
        });
        binding.detergentLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProductsActivity.class)
                        .putExtra("cat","detergents"));
            }
        });
        binding.riceLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProductsActivity.class)
                        .putExtra("cat","grains"));
            }
        });
        binding.oilLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProductsActivity.class)
                        .putExtra("cat","oil"));
            }
        });

    }


    public void scheduleSlider() {
        timer = new Timer();


        final Handler handler = new Handler();

        final Runnable update = new Runnable() {
            public void run() {
                if (page_position == dotscount) {
                    page_position = 0;
                } else {
                    page_position = page_position + 1;
                }
                binding.viewPager.setCurrentItem(page_position, true);
            }
        };

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 500, 4000);
    }

    private void getUserData(View hView){
        TextView username = hView.findViewById(R.id.username);
        TextView e = hView.findViewById(R.id.email);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name = snapshot.child("fullName").getValue().toString();
                    String email = snapshot.child("email").getValue().toString();

                   username.setText(name);
                   e.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStop() {
        timer.cancel();
        super.onStop();
    }

    @Override
    public void onPause() {
        timer.cancel();
        super.onPause();
    }





    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_my_order:
                Intent intent = new Intent(MainActivity.this, MyOrdersActivity.class);
                intent.putExtra("from","customer");
                startActivity(intent);
                binding.drawerLyt.closeDrawer(GravityCompat.START);

                break;
            case R.id.logout:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // user is now signed out
                                Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
                                intent1.putExtra("type","customer");
                                startActivity(intent1);
                                finish();
                            }
                        });
                binding.drawerLyt.closeDrawer(GravityCompat.START);

                break;
            case R.id.cartItems:
                startActivity(new Intent(MainActivity.this, CartActivity.class));
                binding.drawerLyt.closeDrawer(GravityCompat.START);

                break;
            case R.id.nav_profile:
                startActivity(new Intent(MainActivity.this, AccountActivity.class));
                binding.drawerLyt.closeDrawer(GravityCompat.START);

                break;

            case R.id.nav_home:
                binding.drawerLyt.closeDrawer(GravityCompat.START);
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return false;

    }
}