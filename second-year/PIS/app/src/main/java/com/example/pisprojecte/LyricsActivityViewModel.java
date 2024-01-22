package com.example.pisprojecte;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pisprojecte.Entidad.AdaptadorLetra;
import com.example.pisprojecte.Entidad.Letra;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class LyricsActivityViewModel extends AndroidViewModel implements DatabaseAdapter.vmInterface{
    private final MutableLiveData<ArrayList<Letra>> lyrics;
    public static final String TAG = "LyricsActivityViewModel";

    public LyricsActivityViewModel(@NonNull Application application) {
        super(application);
        lyrics = new MutableLiveData<>();
        DatabaseAdapter da = new DatabaseAdapter(this);
        da.getCollectionLyrics();
    }

    public LiveData<ArrayList<Letra>> getArrayLyrics(){
        return lyrics;
    }
    public void addLyric(String title, String text, String owner, boolean publico){
        Letra a = new Letra(title,text,owner,publico);
        ArrayList<Letra> e = lyrics.getValue();
        e.add(a);
        lyrics.setValue(e);



    }
    public Letra getLyric(int idx){
        return lyrics.getValue().get(idx);
    }
    public void saveDocument(String title, String text, String owner, boolean publico){
        DatabaseAdapter da = new DatabaseAdapter(this);
        da.saveDocument(title,text,owner,publico);
        addLyric(title,text,owner,publico);



    }
    public void editLyric(String title, String text, String owner, boolean publico,int idx){
        Letra a = getLyric(idx);
        DatabaseAdapter da = new DatabaseAdapter(this);
        QueryDocumentSnapshot document = da.getOneDocument(a.getTitle());
        String id = document.getId();
        da.editDocument(title, text, owner, publico,id);




    }



    @Override
    public void setCollection(ArrayList<Letra> ac) {
        lyrics.setValue(ac);


    }


}