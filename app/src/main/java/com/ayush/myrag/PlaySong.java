package com.ayush.myrag;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/*
 * This class is basically used to provide all the functionalities for the user like play,pause,next,previous etc.
 * also this class have methods to monitor seek bar progress as well which will display the progress of the song.
 */
public class PlaySong extends AppCompatActivity {
    @Override
    /*
    if user press back button then the activity will destroy , while closing we will stop the media player
    and release it so that it can be assigned to another media file.
     */
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    SeekBar seekBar;
    TextView textView;
    ImageView play,previous,next;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent;
    int position;
    Thread updateSeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        textView = findViewById(R.id.textView);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);

        /*
        we will get all the extra information that has been provided along with intents from Mainactivity class in bundle.
         */
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        // Extracting the list of all songs from the bundle
        songs = (ArrayList) bundle.getParcelableArrayList("songList");
        // setting the text as the name of the current song(choosen by user)
        textContent = intent.getStringExtra("currentSong");
        textView.setText(textContent);
        textView.setSelected(true);
        position = intent.getIntExtra("position",0);
        /*
        Uri stands for uniform resource identifier.
        A uniform resource identifier is a sequence of characters used for identification of a particular resource.
         */
        Uri uri = Uri.parse(songs.get(position).toString());
        //Then will start to play the media file
        mediaPlayer = MediaPlayer.create(this,uri);
        mediaPlayer.start();
        //will set the seekbar max limit to the duration of the current song
        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                 mediaPlayer.seekTo(seekBar.getProgress());
            }
        });


        /*
        If play button is pressed then we will change the image as well as pause the song if song is currently playing.
        if song is pause currently then we will change the image and start the song.
         */
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else {
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }

            }
        });

     /*
     * If previous button is pressed then we will pause the current song and play the previous song in the list.
     * In circular manner
      * */
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();

                if(position!=0){
                    position-=1;
                }
                else{
                    position = songs.size()-1;
                }
                // playing the previous song
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                textContent = songs.get(position).getName();
                textView.setText(textContent);

            }
        });

        /*
         * If next button is pressed then we will pause the current song and play the next song in the list.
         * In circular manner
         */
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();

                if(position!=songs.size()-1){
                    position+=1;
                }
                else{
                    position = 0;
                }
                // playing the next song
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                textContent = songs.get(position).getName();
                textView.setText(textContent);

            }
        });
        /*
        with the help of thread we will update the seekbar in every 500 milliseconds.
         */
        updateSeek = new Thread(){
            @Override
            public void run() {
                int currentPosition = 0;
                try{
                    while (currentPosition < mediaPlayer.getDuration()){
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(500);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();
    }
}