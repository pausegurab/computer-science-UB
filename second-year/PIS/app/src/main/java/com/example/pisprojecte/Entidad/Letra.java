package com.example.pisprojecte.Entidad;

public class Letra {
    private String title;
    private String text;
    private String owner;
    private boolean publico;

    public Letra(String title, String text,String owner, boolean publico) {
        this.title = title;
        this.text = text;
        this.owner = owner;
        this.publico = publico;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String nombreLetra) {
        this.title = nombreLetra;
    }

    public String getText() {
        return text;
    }

    public void setText(String text_lyrics) {
        this.text = text_lyrics;
    }

    public boolean getPublico(){
        return publico;
    }

}
