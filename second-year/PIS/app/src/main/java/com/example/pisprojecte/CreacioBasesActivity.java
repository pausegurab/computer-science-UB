package com.example.pisprojecte;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class CreacioBasesActivity extends AppCompatActivity {
    Button back;
    MediaPlayer base1_110bpm,base2_110bpm,base3_110bpm,ritmo1_110bpm,ritmo2_110bpm,ritmo3_110bpm,ritmo4_110bpm;
    MediaPlayer base1_120bpm,base2_120bpm,base3_120bpm,ritmo1_120bpm,ritmo2_120bpm,ritmo3_120bpm,ritmo4_120bpm;
    MediaPlayer base1_130bpm,base2_130bpm,base3_130bpm,ritmo1_130bpm,ritmo2_130bpm,ritmo3_130bpm,ritmo4_130bpm;
    MediaPlayer base1_140bpm,base2_140bpm,base3_140bpm,ritmo1_140bpm,ritmo2_140bpm,ritmo3_140bpm,ritmo4_140bpm;

    Button button00, button01, button02,button03,button04,button05,button06;
    Button button10, button11,button12,button13,button14,button15,button16;
    Button button20,button21,button22,button23,button24,button25,button26;
    Button button30,button31,button32,button33,button34,button35,button36;
    Button state;
    ArrayList<String> buttons = new ArrayList<String>();
    ArrayList<String> sounds = new ArrayList<String>();
    InputStream file;
    ImageButton record;
    ImageButton stop;
    private MediaRecorder recorder;
    private boolean isRecording = false;
    String fileName;
    private final String [] permissions = {Manifest.permission.RECORD_AUDIO};

    FirebaseUser user;
    DatabaseReference myRef;
    StorageReference basesRef;
    private FirebaseAuth mAuth;
    String nombre;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creaciobases);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ActivityCompat.requestPermissions(this,permissions,200);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(user.getUid());

        basesRef = FirebaseStorage.getInstance().getReference().child("basesCreadas");






        // 1
        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // 2
            String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE};

            //3
            ActivityCompat.requestPermissions(this, permissions,0);

        }*/



        back = findViewById(R.id.button2);
        state = findViewById(R.id.stateButton);
        button00 = findViewById(R.id.button00);
        button01 = findViewById(R.id.button01);
        button02 = findViewById(R.id.button02);
        button03 = findViewById(R.id.button03);
        button04 = findViewById(R.id.button04);
        button05 = findViewById(R.id.button05);
        button06 = findViewById(R.id.button06);
        button10 = findViewById(R.id.button10);
        button11 = findViewById(R.id.button11);
        button12 = findViewById(R.id.button12);
        button13 = findViewById(R.id.button13);
        button14 = findViewById(R.id.button14);
        button15 = findViewById(R.id.button15);
        button16 = findViewById(R.id.button16);
        button20 = findViewById(R.id.button20);
        button21 = findViewById(R.id.button21);
        button22 = findViewById(R.id.button22);
        button23 = findViewById(R.id.button23);
        button24 = findViewById(R.id.button24);
        button25 = findViewById(R.id.button25);
        button26 = findViewById(R.id.button26);
        button30 = findViewById(R.id.button30);
        button31 = findViewById(R.id.button31);
        button32 = findViewById(R.id.button32);
        button33 = findViewById(R.id.button33);
        button34 = findViewById(R.id.button34);
        button35 = findViewById(R.id.button35);
        button36 = findViewById(R.id.button36);
        record = findViewById(R.id.recordButton);
        stop = findViewById(R.id.stopButton);

        base1_110bpm = MediaPlayer.create(this,R.raw.base1_110bpm);
        base1_110bpm = MediaPlayer.create(this,R.raw.base2_110bpm);
        base2_110bpm = MediaPlayer.create(this,R.raw.base2_110bpm);
        base3_110bpm = MediaPlayer.create(this,R.raw.base3_110bpm);
        ritmo1_110bpm = MediaPlayer.create(this,R.raw.ritmo1_110bpm);
        ritmo2_110bpm = MediaPlayer.create(this,R.raw.ritmo2_110bpm);
        ritmo3_110bpm = MediaPlayer.create(this,R.raw.ritmo3_110bpm);
        ritmo4_110bpm = MediaPlayer.create(this,R.raw.ritmo4_110bpm);
        base1_120bpm = MediaPlayer.create(this,R.raw.base1_120bpm);
        base2_120bpm = MediaPlayer.create(this,R.raw.base2_120bpm);
        base3_120bpm = MediaPlayer.create(this,R.raw.base3_120bpm);
        ritmo1_120bpm = MediaPlayer.create(this,R.raw.ritmo1_120bpm);
        ritmo2_120bpm = MediaPlayer.create(this,R.raw.ritmo2_120bpm);
        ritmo3_120bpm = MediaPlayer.create(this,R.raw.ritmo3_120bpm);
        ritmo4_120bpm = MediaPlayer.create(this,R.raw.ritmo4_120bpm);
        base1_130bpm = MediaPlayer.create(this,R.raw.base1_130bpm);
        base2_130bpm = MediaPlayer.create(this,R.raw.base2_130bpm);
        base3_130bpm = MediaPlayer.create(this,R.raw.base3_130bpm);
        ritmo1_130bpm = MediaPlayer.create(this,R.raw.ritmo1_130bpm);
        ritmo2_130bpm = MediaPlayer.create(this,R.raw.ritmo2_130bpm);
        ritmo3_130bpm = MediaPlayer.create(this,R.raw.ritmo3_130bpm);
        ritmo4_130bpm = MediaPlayer.create(this,R.raw.ritmo4_130bpm);
        base1_140bpm = MediaPlayer.create(this,R.raw.base1_140bpm);
        base2_140bpm = MediaPlayer.create(this,R.raw.base2_140bpm);
        base3_140bpm = MediaPlayer.create(this,R.raw.base3_140bpm);
        ritmo1_140bpm = MediaPlayer.create(this,R.raw.ritmo1_140bpm);
        ritmo2_140bpm = MediaPlayer.create(this,R.raw.ritmo2_140bpm);
        ritmo3_140bpm = MediaPlayer.create(this,R.raw.ritmo3_140bpm);
        ritmo4_140bpm = MediaPlayer.create(this,R.raw.ritmo4_140bpm);



        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("startRecording", "startRecording");



                DateFormat df = new SimpleDateFormat("yyMMddHHmmss", Locale.GERMANY);
                String date = df.format(Calendar.getInstance().getTime());



                fileName =  getExternalCacheDir().getAbsolutePath()+ File.separator +date+".mp3";
                date += ".mp3";
                nombre = "/" + date;

                Log.d("startRecording", fileName);

                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_PERFORMANCE);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                recorder.setOutputFile(fileName);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                try {
                    state.setText("Recording...");
                    recorder.prepare();
                    recorder.start();
                    isRecording = true;
                } catch (IOException e) {
                    Log.e("ERROR GRABACIÓN AUDIO: ", e.toString());
                }


            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                state.setText("");
                recorder.stop();
                recorder.release();
                recorder = null;
                isRecording = false;

                StorageReference filePath = basesRef.child(user.getUid()).child(nombre);
                Uri uri = Uri.fromFile(new File(fileName));
                filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "Base Subida a FIREBASE", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreacioBasesActivity.this, InicioActivity.class);
                startActivity(intent);
            }
        });
        /*
        try {
            listeners(buttons,sounds);
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        button00.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        base1_110bpm.setLooping(true);
                        base1_110bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        base1_110bpm.pause();
                        base1_110bpm.seekTo(0);

                }
                return true;

            }
        });

        button01.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        base2_110bpm.setLooping(true);
                        base2_110bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        base2_110bpm.pause();
                        base2_110bpm.seekTo(0);

                }
                return true;

            }
        });
        button02.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        base3_110bpm.setLooping(true);
                        base3_110bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        base3_110bpm.pause();
                        base3_110bpm.seekTo(0);

                }
                return true;

            }
        });
        button03.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ritmo1_110bpm.setLooping(true);
                        ritmo1_110bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        ritmo1_110bpm.pause();
                        ritmo1_110bpm.seekTo(0);

                }
                return true;

            }
        });
        button04.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ritmo2_110bpm.setLooping(true);
                        ritmo2_110bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        ritmo2_110bpm.pause();
                        ritmo2_110bpm.seekTo(0);

                }
                return true;

            }
        });
        button05.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ritmo3_110bpm.setLooping(true);
                        ritmo3_110bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        ritmo3_110bpm.pause();
                        ritmo3_110bpm.seekTo(0);

                }
                return true;

            }
        });
        button06.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ritmo4_110bpm.setLooping(true);
                        ritmo4_110bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        ritmo4_110bpm.pause();
                        ritmo4_110bpm.seekTo(0);

                }
                return true;

            }
        });
        button10.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        base1_120bpm.setLooping(true);
                        base1_120bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        base1_120bpm.pause();
                        base1_120bpm.seekTo(0);

                }
                return true;

            }
        });
        button11.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        base2_120bpm.setLooping(true);
                        base2_120bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        base2_120bpm.pause();
                        base2_120bpm.seekTo(0);

                }
                return true;

            }
        });
        button12.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        base3_120bpm.setLooping(true);
                        base3_120bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        base3_120bpm.pause();
                        base3_120bpm.seekTo(0);

                }
                return true;

            }
        });
        button13.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ritmo1_120bpm.setLooping(true);
                        ritmo1_120bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        ritmo1_120bpm.pause();
                        ritmo1_120bpm.seekTo(0);

                }
                return true;

            }
        });
        button14.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ritmo2_120bpm.setLooping(true);
                        ritmo2_120bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        ritmo2_120bpm.pause();
                        ritmo2_120bpm.seekTo(0);

                }
                return true;

            }
        });
        button15.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ritmo3_120bpm.setLooping(true);
                        ritmo3_120bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        ritmo3_120bpm.pause();
                        ritmo3_120bpm.seekTo(0);

                }
                return true;

            }
        });
        button16.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ritmo4_120bpm.setLooping(true);
                        ritmo4_120bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        ritmo4_120bpm.pause();
                        ritmo4_120bpm.seekTo(0);

                }
                return true;

            }
        });
        button20.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        base1_130bpm.setLooping(true);
                        base1_130bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        base1_130bpm.pause();
                        base1_130bpm.seekTo(0);

                }
                return true;

            }
        });
        button21.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        base2_130bpm.setLooping(true);
                        base2_130bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        base2_130bpm.pause();
                        base2_130bpm.seekTo(0);

                }
                return true;

            }
        });
        button22.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        base3_130bpm.setLooping(true);
                        base3_130bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        base3_130bpm.pause();
                        base3_130bpm.seekTo(0);

                }
                return true;

            }
        });
        button23.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ritmo1_130bpm.setLooping(true);
                        ritmo1_130bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        ritmo1_130bpm.pause();
                        ritmo1_130bpm.seekTo(0);

                }
                return true;

            }
        });
        button24.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ritmo2_130bpm.setLooping(true);
                        ritmo2_130bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        ritmo2_130bpm.pause();
                        ritmo2_130bpm.seekTo(0);

                }
                return true;

            }
        });
        button25.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ritmo3_130bpm.setLooping(true);
                        ritmo3_130bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        ritmo3_130bpm.pause();
                        ritmo3_130bpm.seekTo(0);

                }
                return true;

            }
        });
        button26.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ritmo4_130bpm.setLooping(true);
                        ritmo4_130bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        ritmo4_130bpm.pause();
                        ritmo4_130bpm.seekTo(0);

                }
                return true;

            }
        });
        button30.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        base1_140bpm.setLooping(true);
                        base1_140bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        base1_140bpm.pause();
                        base1_140bpm.seekTo(0);

                }
                return true;

            }
        });
        button31.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        base2_140bpm.setLooping(true);
                        base2_140bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        base2_140bpm.pause();
                        base2_140bpm.seekTo(0);

                }
                return true;

            }
        });
        button32.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        base3_140bpm.setLooping(true);
                        base3_140bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        base3_140bpm.pause();
                        base3_140bpm.seekTo(0);

                }
                return true;

            }
        });
        button33.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ritmo1_140bpm.setLooping(true);
                        ritmo1_140bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        ritmo1_140bpm.pause();
                        ritmo1_140bpm.seekTo(0);

                }
                return true;

            }
        });
        button34.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ritmo2_140bpm.setLooping(true);
                        ritmo2_140bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        ritmo2_140bpm.pause();
                        ritmo2_140bpm.seekTo(0);

                }
                return true;

            }
        });
        button35.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ritmo3_140bpm.setLooping(true);
                        ritmo3_140bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        ritmo3_140bpm.pause();
                        ritmo3_140bpm.seekTo(0);

                }
                return true;

            }
        });
        button36.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ritmo4_140bpm.setLooping(true);
                        ritmo4_140bpm.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        ritmo4_140bpm.pause();
                        ritmo4_140bpm.seekTo(0);

                }
                return true;

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            Uri fileUri = data.getData();

            StorageReference folder = FirebaseStorage.getInstance().getReference().child("imagenesPerfil");

            final StorageReference file_name = folder.child(user.getUid());

            file_name.putFile(fileUri).addOnSuccessListener(taskSnapshot -> file_name.getDownloadUrl().addOnSuccessListener(uri -> {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("link", String.valueOf(uri));
                myRef.setValue(hashMap);
                Log.d("Mensaje", "Se subió correctamente");

            }));
        }
    }






   /*
    public void listeners() throws IOException {
        file = this.getAssets().open("listenersBases.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file));
        String curLine;
        Button button = findViewById(R.id.button00);

        int i = 0;

        while ((curLine=bufferedReader.readLine())!= null){
            if (i %2 == 0){
                int butIf = getResources().getIdentifier(curLine, "id", getPackageName());
                button  = findViewById(butIf);




                i++;
            }else{
                int baseId = getResources().getIdentifier(curLine,"id",getPackageName());
                MediaPlayer base = MediaPlayer.create(this,baseId);
                button.setOnTouchListener((view, motionEvent) -> {
                    switch (motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            base.setLooping(true);
                            base.start();
                            break;
                        case MotionEvent.ACTION_UP:
                            base.pause();
                            base.seekTo(0);

                    }
                    return true;

                });
                i++;
            }*/
        }








