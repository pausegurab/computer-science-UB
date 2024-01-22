package com.example.pisprojecte;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
//import android.media.MediaPlayer;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

//import android.os.Environment;


import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toast;

import java.io.File;
import java.io.IOException;
//import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

//import static android.Manifest.permission.RECORD_AUDIO;
//import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


//import android.content.pm.PackageManager;

//import androidx.annotation.NonNull;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pisprojecte.Entidad.AdaptadorSonido;
import com.example.pisprojecte.Entidad.Sonido;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class EstudioActivity extends AppCompatActivity implements DialogElegirLyric.DialogElegirListener {



    Button back, listadoLetrasButton, stopButton, recordButton;
    TextView songText;
    RecyclerView bases;
    private MediaRecorder recorder;
    MediaPlayer mediaPlayer;
    private boolean isRecording = false;
    String fileName;
    AdaptadorSonido adapter;

    ArrayList<Sonido> arrayBases;
    private final String [] permissions = {Manifest.permission.RECORD_AUDIO};

    FirebaseUser user;
    DatabaseReference myRef;
    StorageReference basesRef;
    StorageReference cancionesRef;
    private FirebaseAuth mAuth;
    String nombre;
    ArrayList<Sonido> arrayBasesFirebase;



    public void goToInicio(View view){
        Intent intent = new Intent(this, InicioActivity.class);
        startActivity(intent);
    }


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estudio);
        ActivityCompat.requestPermissions(this, permissions, 200);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(user.getUid());

        basesRef = FirebaseStorage.getInstance().getReference().child("basesCreadas");
        cancionesRef = FirebaseStorage.getInstance().getReference().child("cancionesCreadas");

        arrayBases = new ArrayList<>();

        back = findViewById(R.id.button_Back_Estudio);
        listadoLetrasButton = findViewById(R.id.chooseSong);
        stopButton = findViewById(R.id.restart);
        recordButton = findViewById(R.id.start);
        bases = findViewById(R.id.listado_grabaciones_recyclerView);
        songText = findViewById(R.id.textView_Lyrics);
        songText.setMovementMethod(new ScrollingMovementMethod());





        listadoLetrasButton.setOnClickListener(view -> openDialog());




        rellenarArrayBasesPredefinidas();





        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EstudioActivity.this, InicioActivity.class);
                startActivity(intent);
                mediaPlayer = adapter.getMediaPlayer();
                if(mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                }
            }
        });

        recordButton.setOnClickListener(new View.OnClickListener() {
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
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                recorder.setOutputFile(fileName);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                try {
                    Toast.makeText(getApplicationContext(), "Grabando...", Toast.LENGTH_SHORT).show();
                    recorder.prepare();
                    recorder.start();
                    isRecording = true;
                } catch (IOException e) {
                    Log.e("ERROR GRABACIÓN AUDIO: ", e.toString());
                }


            }

        });


        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recorder.stop();
                recorder.release();
                recorder = null;
                isRecording = false;

                StorageReference filePath = cancionesRef.child(user.getUid()).child(nombre);
                Uri uri = Uri.fromFile(new File(fileName));
                filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "Canción Subida a FIREBASE", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

/*
        buttonStart.setOnClickListener(view -> {

            if(checkPermission()) {

                AudioSavePathInDevice =
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                CreateRandomAudioFileName(5) + "AudioRecording.3gp";

                MediaRecorderReady();

                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IllegalStateException | IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                buttonStart.setEnabled(false);
                buttonStop.setEnabled(true);

                Toast.makeText(EstudioActivity.this, "Recording started",
                        Toast.LENGTH_LONG).show();
            } else {
                requestPermission();
            }

        });

        buttonStop.setOnClickListener(view -> {
            mediaRecorder.stop();
            buttonStop.setEnabled(false);
            //buttonPlayLastRecordAudio.setEnabled(true);
            buttonStart.setEnabled(true);
            playPauseButton.setEnabled(false);

            Toast.makeText(EstudioActivity.this, "Recording Completed",
                    Toast.LENGTH_LONG).show();
        });



        playPauseButton.setOnClickListener(view -> {
            buttonStop.setEnabled(false);
            buttonStart.setEnabled(true);
            playPauseButton.setEnabled(false);
            //buttonPlayLastRecordAudio.setEnabled(true);

            if(mediaPlayer != null){
                mediaPlayer.stop();
                mediaPlayer.release();
                MediaRecorderReady();
            }
        });

    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    public String CreateRandomAudioFileName(int string){
        StringBuilder stringBuilder = new StringBuilder( string );
        int i = 0 ;
        while(i < string ) {
            String randomAudioFileName = "ABCDEFGHIJKLMNOP";
            stringBuilder.append(randomAudioFileName.
                    charAt(random.nextInt(randomAudioFileName.length())));

            i++ ;
        }
        return stringBuilder.toString();
    }
    */

/*
    private void requestPermission() {
        ActivityCompat.requestPermissions(EstudioActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RequestPermissionCode) {
            if (grantResults.length > 0) {
                boolean StoragePermission = grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED;
                boolean RecordPermission = grantResults[1] ==
                        PackageManager.PERMISSION_GRANTED;

                if (StoragePermission && RecordPermission) {
                    Toast.makeText(EstudioActivity.this, "Permission Granted",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(EstudioActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }


 */


    }

    public void openDialog(){
        DialogElegirLyric elegirLyric = new DialogElegirLyric();
        elegirLyric.show(getSupportFragmentManager(), "Elegir Lyrics dialog");
    }


    public void setLyricsEstudio(String text){
        songText.setText(text);


    }



    private void rellenarArrayBasesPredefinidas(){
        bases.setLayoutManager(new LinearLayoutManager(this));

        arrayBases.add(new Sonido("Base 1",  R.raw.base_bombo_caja));
        arrayBases.add(new Sonido("Base 2",  R.raw.base_chill));
        arrayBases.add(new Sonido("Base 3",  R.raw.base_mina));
        arrayBases.add(new Sonido("Base 4",  R.raw.base_rap_triste));

        //Afegir les cançons que obtinguem de la Firebase

        basesRef.child(user.getUid()).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    arrayBases.add(new Sonido("Base " + item.getName(), item.hashCode()));;

                }

                adapter = new AdaptadorSonido(EstudioActivity.this, arrayBases);
                bases.setAdapter(adapter);


                Toast.makeText(getApplicationContext(), "Correcta rellenada de array Firebase", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Fallo de array Firebase", Toast.LENGTH_LONG).show();
            }
        });

/*



        try {
            for(int i = 0; i<arrayBasesFirebase.size(); i++) {
                arrayBases.add(arrayBasesFirebase.get(i));

            }
            Toast.makeText(getApplicationContext(), "UNION CORRECTA ARRAYS", Toast.LENGTH_LONG).show();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "NO UNIDO ARRAYS", Toast.LENGTH_LONG).show();

        }

 */



    }

}