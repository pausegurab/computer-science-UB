package com.example.pisprojecte.Entidad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pisprojecte.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdaptadorSonido extends RecyclerView.Adapter<AdaptadorSonido.BaseViewHolder> {

    MediaPlayer mediaPlayer;
    Context context;
    ArrayList<Sonido> arrayBases;
    ArrayList<Sonido> arrayBasesOriginal;
    private int selectedPos = RecyclerView.NO_POSITION;

    public AdaptadorSonido(Context context, ArrayList<Sonido> arrayBases) {
        this.context = context;
        this.arrayBases = arrayBases;
        this.arrayBasesOriginal = new ArrayList<>();
        arrayBasesOriginal.addAll(arrayBases);
    }

    @NonNull
    @Override
    public AdaptadorSonido.BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modelo_reproduccion_recycler_view, parent, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorSonido.BaseViewHolder holder, @SuppressLint("RecyclerView") int i) {
        holder.textViewNombreBase.setText(arrayBases.get(i).getNombreBase());
        holder.itemView.setSelected(selectedPos == i);
        holder.itemView.setBackgroundColor(selectedPos == i ? Color.rgb(248,95,106) : Color.TRANSPARENT);
        holder.botonPlayBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null){
                    mediaPlayer.stop();
                }
                try{
                    mediaPlayer = MediaPlayer.create(context, arrayBases.get(i).getGeneral());
                    mediaPlayer.start();
                }catch(Exception e){
                    Toast.makeText(context, "No se ha podido reproducir", Toast.LENGTH_LONG).show();
                }




            }

        });


        holder.botonPauseBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
            }
        });

    }
    public void filter(final String strSearch) {
        if (strSearch.length() == 0) {
            arrayBases.clear();
            arrayBases.addAll(arrayBasesOriginal);
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                arrayBases.clear();
                List<Sonido> collect = arrayBasesOriginal.stream()
                        .filter(i -> i.getNombreBase().toLowerCase().contains(strSearch))
                        .collect(Collectors.toList());

                arrayBases.addAll(collect);
            }
            else {
                arrayBases.clear();
                for (Sonido i : arrayBasesOriginal) {
                    if (i.getNombreBase().toLowerCase().contains(strSearch)) {
                        arrayBases.add(i);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return arrayBases.size();
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textViewNombreBase;
        ImageButton botonPlayBase;
        ImageButton botonPauseBase;

        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombreBase = itemView.findViewById(R.id.text_title_base);
            botonPauseBase = itemView.findViewById(R.id.pause_button);
            botonPlayBase = itemView.findViewById(R.id.icono_letra);
        }

        @Override
        public void onClick(View view) {
            notifyItemChanged(selectedPos);
            selectedPos = getLayoutPosition();
            notifyItemChanged(selectedPos);
        }



    }
    public MediaPlayer getMediaPlayer() {
        if(mediaPlayer != null) {
            return mediaPlayer;
        }else{
            return null;
        }
    }


}
