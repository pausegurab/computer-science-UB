package com.example.pisprojecte;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.pisprojecte.Entidad.Letra;

import java.util.ArrayList;

public class DialogElegirLyricViewModel extends AndroidViewModel implements DatabaseAdapter.vmInterface{
    private final MutableLiveData<ArrayList<Letra>> lyrics;
    public static final String TAG = "DialogElegirLyricViewModel";

    public DialogElegirLyricViewModel(@NonNull Application application) {
        super(application);
        lyrics = new MutableLiveData<>();
        DatabaseAdapter da = new DatabaseAdapter(this);
        da.getCollectionLyrics();
    }

    public LiveData<ArrayList<Letra>> getArrayLyrics(){
        return lyrics;
    }

    public Letra getLyric(int idx){
        return lyrics.getValue().get(idx);
    }




    @Override
    public void setCollection(ArrayList<Letra> ac) {
        lyrics.setValue(ac);


    }


}
