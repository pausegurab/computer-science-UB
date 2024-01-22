package com.example.pisprojecte;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
//import android.content.DialogInterface;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pisprojecte.Entidad.AdaptadorLetra;
import com.example.pisprojecte.Entidad.Letra;

import java.util.ArrayList;
//import java.util.Objects;

public class DialogElegirLyric extends AppCompatDialogFragment implements  AdaptadorLetra.onClickRecycler, SearchView.OnQueryTextListener {

    RecyclerView listadoLetras;
    private DialogElegirLyricViewModel viewModel;
    TextView textView_Lyrics;
    AdaptadorLetra adapter;
    private DialogElegirListener listener;
    int indexCurrentLetra = 0;
    SearchView barraBusquedaLetras;



    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //int indexCurrentLetra = -1;
        //Context parentContext = this.getContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_elegir_lyrics, null);
        barraBusquedaLetras = view.findViewById(R.id.nombre_lyric_dialog);


        builder.setView(view).setTitle("Escoge una letra").setNegativeButton("Cancelar", (dialogInterface, i) -> {

        }).setPositiveButton("Escoger", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Letra a = viewModel.getLyric(indexCurrentLetra);
                listener.setLyricsEstudio(a.getText());
            }

        });
        listadoLetras = view.findViewById(R.id.recyclerview);
        listadoLetras.setLayoutManager(new LinearLayoutManager(getActivity()));
        textView_Lyrics = view.findViewById(R.id.textView_Lyrics);
        setLiveDataObservers();
        barraBusquedaLetras.setOnQueryTextListener((SearchView.OnQueryTextListener) this);
        return builder.create();


    }

    public void setLiveDataObservers() {
        //Subscribe the activity to the observable
        viewModel = new ViewModelProvider(this).get(DialogElegirLyricViewModel.class);

       final Observer<ArrayList<Letra>> observer = new Observer<ArrayList<Letra>>() {
           @Override
           public void onChanged(ArrayList<Letra> ac) {
               adapter = new AdaptadorLetra(getActivity(), ac, DialogElegirLyric.this);
               listadoLetras.swapAdapter(adapter, false);
               adapter.notifyDataSetChanged();
           }
        };
        viewModel.getArrayLyrics().observe(this, observer);
    }

    public void onLyricClick(int i) {
        indexCurrentLetra = i;

    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try {
            listener = (DialogElegirLyric.DialogElegirListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "debes implementar DialogElegirListener");
        }
    }





    public interface DialogElegirListener {
        void setLyricsEstudio(String letra);
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