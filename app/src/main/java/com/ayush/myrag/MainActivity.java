package com.ayush.myrag;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

/*
* In this project I have created a song player which reads songs from the storage of the phone and show them all
* in a simple list view, where user can select any song from the list and then user can do various things in it like
* user can play or pause the song user can choose next/previous song and activity is provided with impressive UI.
* I have used Dexter library in this application
*  Dexter is an android library that simplifies the process of requesting permissions at runtime.
* we can ask the user while running the application weather user want to deny or allow for the permissions required.
* */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ListView listView;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listview);

        //Asking user to allow the application to read storage
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    /* if user grants the permission then we will read all mp3 files available in the device storage
                    *  for this we will use File class
                    * Java File class represents the files and directories path names in an abstract manner.
                    * This class is used for creation of files and directories, file searching, file deletion etc.
                    * The File object represents the actual file/directory on the disk.
                    * */
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                        Toast.makeText(MainActivity.this,"Runtime Permission Given",Toast.LENGTH_SHORT).show();
                        /* now we will collect all the mp3 files in mysongs named array list with the help of fetchsongs method
                           and then in a string array we will stroe the names of all the songs to populate the list
                        * */
                        ArrayList<File> mySongs = fetchSongs(Environment.getExternalStorageDirectory());
                        String[] items = new String[mySongs.size()];
                        for(int i=0;i<mySongs.size();i++){
                            items[i] = mySongs.get(i).getName().replace(".mp3","");
                        }
                        /*
                        * After extracting names of all the songs we will used default adapter and populate the
                        * list with the names of the songs, with the help of which user can choose any song*/
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,items);
                        listView.setAdapter(adapter);

                        /*
                        after populating, if user click in any of the song user will be taken to new activity with the
                        help of intent and we will also provide some extra information along with the desired song like
                        list of songs, and it's position.
                        Now we will start the new activity
                         */
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(MainActivity.this,PlaySong.class);
                                String currentSong = listView.getItemAtPosition(position).toString();
                                intent.putExtra("songList",mySongs);
                                intent.putExtra("currentSong",currentSong);
                                intent.putExtra("position",position);
                                startActivity(intent);
                            }
                        });
                     }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                               permissionToken.cancelPermissionRequest();
                    }
                })
                .check();
    }

    /*
    * fetchsongs() method is the backbone of this whole application , as it takes the whole file directory as input
    * and returns the list of only valid .mp3 extension files.
     */
    public ArrayList<File> fetchSongs(File file){
        ArrayList arrayList = new ArrayList();
        //getting the flist of files/directory in songs array
        File[] songs = file.listFiles();
        if(songs != null){
            for(File myfile : songs){
                // if any file in songs array is a directory then we will recursively call method again to read files inside the directory
                if(myfile.isDirectory() && (!myfile.isHidden())){
                    arrayList.addAll(fetchSongs(myfile));
                }
                else {
                    // only accept those files that have extension .mp3
                    if(myfile.getName().endsWith(".mp3") && !myfile.getName().startsWith(".")){
                        arrayList.add(myfile);
                    }
                }
            }
        }
        return arrayList;
    }

}