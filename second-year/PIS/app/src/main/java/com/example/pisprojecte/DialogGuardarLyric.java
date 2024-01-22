package com.example.pisprojecte;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

public class DialogGuardarLyric extends AppCompatDialogFragment {
    private EditText nombreCancion;
    private DialogGuardarListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_guardar_lyrics, null);

        builder.setView(view).setTitle("Guardar nueva letra").setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String nombreLetra = nombreCancion.getText().toString();
                listener.guardarLyricRecycler(nombreLetra);
            }
        });

        nombreCancion = view.findViewById(R.id.nombre_lyric_dialog);
        return builder.create();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try {
            listener = (DialogGuardarListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "debes implementar DialogGuardarListener");
        }
    }

    public interface DialogGuardarListener {
        void guardarLyricRecycler(String nombreLetra);
    }
}
