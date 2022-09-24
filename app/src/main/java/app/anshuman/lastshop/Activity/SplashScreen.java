package app.anshuman.lastshop.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import app.anshuman.lastshop.Authentication.ReferenceActivity;
import app.anshuman.lastshop.R;
import app.anshuman.lastshop.databinding.ActivitySplashScreenBinding;

public class SplashScreen extends AppCompatActivity {

    ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());




        Thread thread=new Thread()
        {

            public  void run(){

                try {
                    sleep(4000);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally {
                    Intent intent= new Intent(SplashScreen.this,ReferenceActivity.class);
                    startActivity(intent);
                    finish();

                }


            }


        }; thread.start();



    }
}