
package com.oligo.t4.demoai_2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
ImageView logo;
Animation animacion;
    public static String BaseUrl = "http://api.openweathermap.org/";
    public static String AppId = "642c99084a22d7b3a608ac438f7be6b8";
    public static String lat = "20.684084";
    public static String lon = "-101.355080";
private Handler manejador=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logo = (ImageView)findViewById(R.id.image_logo);
        animacion = AnimationUtils.loadAnimation(MainActivity.this ,R.anim.blink_animation);
        logo.startAnimation(animacion);
       manejador.postDelayed(new Runnable() {
           @Override
           public void run() {
           try {
               Intent intent = new Intent(MainActivity.this , menu_p.class);
              startActivity(intent);
               finish();
           }
           catch (Exception ignored)
           {
               ignored.printStackTrace();
           }
           }
       },3000);


    }


}


