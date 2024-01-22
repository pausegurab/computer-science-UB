package com.example.pisprojecte.Entidad;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pisprojecte.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AdaptadorLetra extends RecyclerView.Adapter<AdaptadorLetra.LetraViewHolder> {

    Context context;
    ArrayList<Letra> arrayLetrasString;
    ArrayList<Letra> arrayLetrasOriginal;
    private onClickRecycler xOnClickRecycler;
    private int selectedPos = RecyclerView.NO_POSITION;


    /* public AdaptadorLetra(Context context, ArrayList<Letra> arrayLetras, onClickRecycler xOnClickRecycler) {
         this.context = context;
         this.arrayLetras = arrayLetras;
         this.xOnClickRecycler = xOnClickRecycler;
     }*/
    public AdaptadorLetra(Context context, ArrayList<Letra> arrayLetrasString, onClickRecycler xOnClickRecycler) {
        this.context = context;
        this.arrayLetrasString = arrayLetrasString;
        this.arrayLetrasOriginal = new ArrayList<>();
        arrayLetrasOriginal.addAll(arrayLetrasString);
        this.xOnClickRecycler = xOnClickRecycler;
    }


    @NonNull
    @Override
    public AdaptadorLetra.LetraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modelo_letra_recycler_view, parent, false);
        return new LetraViewHolder(view, xOnClickRecycler);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorLetra.LetraViewHolder holder, int i) {
        holder.textLetra.setText(arrayLetrasString.get(i).getTitle());
        holder.itemView.setSelected(selectedPos == i);
        holder.itemView.setBackgroundColor(selectedPos == i ? Color.rgb(248,95,106) : Color.TRANSPARENT);

    }

    @Override
    public int getItemCount() {
        return arrayLetrasString.size();
    }

    public void filter(final String strSearch) {
        if (strSearch.length() == 0) {
            arrayLetrasString.clear();
            arrayLetrasString.addAll(arrayLetrasOriginal);
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                arrayLetrasString.clear();
                List<Letra> collect = arrayLetrasOriginal.stream()
                        .filter(i -> i.getTitle().toLowerCase().contains(strSearch))
                        .collect(Collectors.toList());

                arrayLetrasString.addAll(collect);
            }
            else {
                arrayLetrasString.clear();
                for (Letra i : arrayLetrasOriginal) {
                    if (i.getTitle().toLowerCase().contains(strSearch)) {
                        arrayLetrasString.add(i);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public class LetraViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageButton iconLetra;
        TextView textLetra;
        onClickRecycler onClickRecycler;

        public LetraViewHolder(@NonNull View itemView, onClickRecycler onClickRecycler) {
            super(itemView);
            textLetra = itemView.findViewById(R.id.text_title_base);
            iconLetra = itemView.findViewById(R.id.icono_letra);
            this.onClickRecycler = onClickRecycler;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onClickRecycler.onLyricClick(getAdapterPosition());
            notifyItemChanged(selectedPos);
            selectedPos = getLayoutPosition();
            notifyItemChanged(selectedPos);

        }
    }

    public interface onClickRecycler{
        public void onLyricClick(int i);

    }
}
