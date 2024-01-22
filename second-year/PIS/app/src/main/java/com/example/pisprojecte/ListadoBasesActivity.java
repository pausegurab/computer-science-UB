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

public class ListadoBasesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    SearchView barraBusquedaBases;
    RecyclerView listadoBases;
    Button back_listado;
    Button add_base;
    MediaPlayer mediaPlayer;
    ArrayList<Sonido> arrayBases;
    AdaptadorSonido adapter;
    StorageReference basesRef;
    FirebaseUser user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_bases);
        barraBusquedaBases = findViewById(R.id.buscar_baseListado);
        listadoBases = findViewById(R.id.recycler_listado_bases);
        back_listado = findViewById(R.id.back_listado);
        add_base = findViewById(R.id.add_base);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        basesRef = FirebaseStorage.getInstance().getReference().child("basesCreadas");



        listadoBases.setLayoutManager(new LinearLayoutManager(this));

        arrayBases = new ArrayList<>();
        arrayBases.add(new Sonido("Base 1", R.raw.base_bombo_caja));
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

                adapter = new AdaptadorSonido(ListadoBasesActivity.this, arrayBases);
                listadoBases.setAdapter(adapter);


                Toast.makeText(getApplicationContext(), "Correcta rellenada de array Firebase", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Fallo de array Firebase", Toast.LENGTH_LONG).show();
            }
        });








        back_listado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListadoBasesActivity.this, InicioActivity.class);
                startActivity(intent);
                mediaPlayer = adapter.getMediaPlayer();
                if(mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                }


            }
        });

        add_base.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListadoBasesActivity.this, CreacioBasesActivity.class);
                startActivity(intent);
            }
        });

        barraBusquedaBases.setOnQueryTextListener(this);



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


