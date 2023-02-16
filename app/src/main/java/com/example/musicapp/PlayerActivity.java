package com.example.musicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class PlayerActivity extends AppCompatActivity {
    ImageView prev,playBtn,next;

    TextView txtStart,txtEnd,txtSongName;
    SeekBar seekBar;
    String songName;
    public static final String EXTRA_NAME="song_name";
    static MediaPlayer mediaPlayer;
    int i;
    ArrayList<File>mySongs;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    Thread updateSeekbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player2);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Music Player");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        prev=findViewById(R.id.prev);
        playBtn=findViewById(R.id.playBtn);
        next=findViewById(R.id.next);
        txtEnd=findViewById(R.id.txtend);
        txtSongName=findViewById(R.id.songname);
        txtStart=findViewById(R.id.txtstart);
        seekBar=findViewById(R.id.seekbar);

        if(mediaPlayer!=null){
            mediaPlayer.start();
            mediaPlayer.release();
        }
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        mySongs=(ArrayList)bundle.getParcelableArrayList("songs");
        String sName=intent.getStringExtra("songName");
        i=bundle.getInt("position",0);
        txtSongName.setSelected(true);
        Uri uri=Uri.parse(mySongs.get(i).toString());
        songName=mySongs.get(i).getName();
        txtSongName.setText(songName);

        mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();


        updateSeekbar=new Thread(){
            @Override
            public void run() {
               int totalDuration=mediaPlayer.getDuration();
               int currentPosition=0;
               while (currentPosition<totalDuration){
                   try {
                       sleep(500);
                       currentPosition=mediaPlayer.getCurrentPosition();
                       seekBar.setProgress(currentPosition);

                   }
                   catch (InterruptedException | IllegalStateException e){
                       e.printStackTrace();
                   }
               }

            }
        };
        seekBar.setMax(mediaPlayer.getDuration());
        updateSeekbar.start();
       seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.purple_200), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.purple_200), PorterDuff.Mode.SRC_IN);

       seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
           @Override
           public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

           }

           @Override
           public void onStartTrackingTouch(SeekBar seekBar) {

           }

           @Override
           public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
           }
       });
    String endTime=createTime(mediaPlayer.getDuration());
    txtEnd.setText(endTime);

    final Handler handler=new Handler();
      final int delay=1000;
       handler.postDelayed(new Runnable() {
           @Override
           public void run() {
               String currentTime=createTime(mediaPlayer.getCurrentPosition());
                txtStart.setText(currentTime);
                handler.postDelayed(this,delay);
           }
       },delay);

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    playBtn.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else{
                    playBtn.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                next.performClick();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                i=((i+1)%mySongs.size());
                Uri uri=Uri.parse(mySongs.get(i).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
                songName=mySongs.get(i).getName();
                txtSongName.setText(songName);
                mediaPlayer.start();
                String endTime=createTime(mediaPlayer.getDuration());
                txtEnd.setText(endTime);
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                i=((i-1)<0)?(mySongs.size()-1):i-1;
                Uri uri=Uri.parse(mySongs.get(i).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
                songName=mySongs.get(i).getName();
                txtSongName.setText(songName);
                mediaPlayer.start();
                String endTime=createTime(mediaPlayer.getDuration());
                txtEnd.setText(endTime);
            }
        });

        }
    public String createTime(int duration){
        String time="";
        int min=duration/1000/60;
        int sec=duration/1000%60;
        time=time+min+":";
        if(sec<10){
            time+="0";
        }
        time+=sec;
        return time;
    }

}