package com.example.pisprojecte;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pisprojecte.Entidad.AdaptadorSonido;
import com.example.pisprojecte.Entidad.Sonido;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class ListadoCancionesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    SearchView barraBusquedaCanciones;
    RecyclerView listadoCanciones;
    Button back_listadoCanciones;
    MediaPlayer mediaPlayer;
    ArrayList<Sonido> arrayCanciones;
    AdaptadorSonido adapter;
    StorageReference CancionesRef;
    FirebaseUser user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_canciones);
        barraBusquedaCanciones = findViewById(R.id.buscar_Cancion);
        listadoCanciones = findViewById(R.id.recycler_listado_canciones);
        back_listadoCanciones = findViewById(R.id.back_listado_canciones);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        CancionesRef = FirebaseStorage.getInstance().getReference().child("cancionesCreadas");



        listadoCanciones.setLayoutManager(new LinearLayoutManager(this));

        arrayCanciones = new ArrayList<>();

        //Afegir les cançons que obtinguem de la Firebase


        CancionesRef.child(user.getUid()).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    arrayCanciones.add(new Sonido("Canción " + item.getName() , item.hashCode()));;

                }

                adapter = new AdaptadorSonido(ListadoCancionesActivity.this, arrayCanciones);
                listadoCanciones.setAdapter(adapter);


                Toast.makeText(getApplicationContext(), "Correcta rellenada de array Firebase", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Fallo de array Firebase", Toast.LENGTH_LONG).show();
            }
        });








        back_listadoCanciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListadoCancionesActivity.this, InicioActivity.class);
                startActivity(intent);
                mediaPlayer = adapter.getMediaPlayer();
                if(mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                }


            }
        });



        barraBusquedaCanciones.setOnQueryTextListener(this);

    }




    public void searchCancionesPorNombre(View view){

    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        adapter.filter(s);
        return false;
    }
}