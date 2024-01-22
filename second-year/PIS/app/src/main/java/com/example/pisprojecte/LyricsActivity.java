package com.example.pisprojecte;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pisprojecte.Entidad.AdaptadorLetra;
import com.example.pisprojecte.Entidad.Letra;
import com.example.pisprojecte.Entidad.AdaptadorLetra;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LyricsActivity extends AppCompatActivity  implements  AdaptadorLetra.onClickRecycler, DialogGuardarLyric.DialogGuardarListener,SearchView.OnQueryTextListener  {
    Button back;
    SearchView barraBusquedaLetras;
    RecyclerView listadoLetras;
    Button add_letras;
    TextView textView_nameletra;
    EditText editar_letra;
    Button nuevaLetra;
    LoginActivity loginActivity;
    int indexCurrentLetra;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LyricsActivityViewModel viewModel;
    private Context parentContext;
    AdaptadorLetra adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        indexCurrentLetra = -1;
        setContentView(R.layout.activity_letras);
        back = findViewById(R.id.back_letras);
        parentContext = this.getBaseContext();


        barraBusquedaLetras = findViewById(R.id.buscar_letra);
        listadoLetras = findViewById(R.id.listadoLetras);
        add_letras = findViewById(R.id.button_save);
        nuevaLetra = findViewById(R.id.add_letra);
        editar_letra = findViewById(R.id.textView_editarletra);
        textView_nameletra = findViewById(R.id.textView_nameletra);

        listadoLetras.setLayoutManager(new LinearLayoutManager(this));



        setLiveDataObservers();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LyricsActivity.this, InicioActivity.class);
                startActivity(intent);
            }

        });

        add_letras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Decide whether to launch Dialog or update lyrics
                if(indexCurrentLetra == -1){
                    openDialog();
                }else{
                    String mail = user.getEmail();
                    viewModel.editLyric(textView_nameletra.getText().toString(),editar_letra.getText().toString(),mail,viewModel.getLyric(indexCurrentLetra).getPublico(),indexCurrentLetra);
                }
            }
        });

        nuevaLetra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                indexCurrentLetra = -1;
                editar_letra.setText("");
                textView_nameletra.setText("Nueva Letra");
            }
        });

        barraBusquedaLetras.setOnQueryTextListener(this);


    }


    public void setLiveDataObservers() {
        //Subscribe the activity to the observable
        viewModel = new ViewModelProvider(this).get(LyricsActivityViewModel.class);

        final Observer<ArrayList<Letra>> observer = new Observer<ArrayList<Letra>>() {
            @Override
            public void onChanged(ArrayList<Letra> ac) {
                adapter = new AdaptadorLetra(LyricsActivity.this, ac, LyricsActivity.this::onLyricClick);
                listadoLetras.swapAdapter(adapter, false);

                adapter.notifyDataSetChanged();
            }
        };
        viewModel.getArrayLyrics().observe(this, observer);


    }














    @Override
    public void onLyricClick(int i) {

        indexCurrentLetra = i;
        Letra a = viewModel.getLyric(i);
        editar_letra.setText(a.getText());
        textView_nameletra.setText(a.getTitle());




    }

    public void openDialog(){
        DialogGuardarLyric dialogGuardarLyric = new DialogGuardarLyric();
        dialogGuardarLyric.show(getSupportFragmentManager(), "Guardar Letra");
    }

    @Override
    public void guardarLyricRecycler(String nombreLetra) {
        String mail = user.getEmail();
        viewModel.saveDocument(nombreLetra,editar_letra.getText().toString(),mail,false);





        //arrayLetras.add(new Letra(nombreLetra, editar_letra.getText().toString()));
        editar_letra.setText("");
        indexCurrentLetra = -1; //reset index
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