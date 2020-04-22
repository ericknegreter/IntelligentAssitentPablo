package com.oligo.t4.demoai_2;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.VideoView;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

public class video_java extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageView fab;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video);
        VideoView videoView =(VideoView)findViewById(R.id.video1);
        String path = "android.resource://" + getPackageName() + "/" + R.raw.video_lmv_presentacion;
        videoView.setVideoURI(Uri.parse(path));
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.e("Splash Screen", "Video Completed!!");
            }
        });
    }
@Override
    public void onBackPressed() {

    Intent regresar_intent=new Intent( getApplicationContext(),menu_p.class);
    regresar_intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
    startActivity(regresar_intent);
    }
}
