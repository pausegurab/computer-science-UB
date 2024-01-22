package com.example.pisprojecte.Entidad;

import android.net.Uri;

public class Sonido {
    private String nombreBase;
    private int general;

    public Sonido(String nombreBase, int general) {
        this.nombreBase = nombreBase;
        this.general = general;
    }

    public String getNombreBase() {
        return nombreBase;
    }

    public void setNombreBase(String nombreBase) {
        this.nombreBase = nombreBase;
    }

    public int getGeneral() {
        return general;
    }

    public void setGeneral(int general) {
        this.general = general;
    }
}
