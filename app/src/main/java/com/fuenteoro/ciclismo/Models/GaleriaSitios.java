package com.fuenteoro.ciclismo.Models;

import com.fuenteoro.ciclismo.R;

import java.util.ArrayList;

public class GaleriaSitios {
    private int imagen;
    private String autor;

    public GaleriaSitios(){
        imagen = 0;
        autor = "";
    }

    public GaleriaSitios(int imagen, String autor) {
        this.imagen = imagen;
        this.autor = autor;
    }

    public int getImagen() {
        return imagen;
    }

    public String getAutor() {
        return autor;
    }

    public ArrayList<GaleriaSitios>galeriaSitios(){
        GaleriaSitios galeriaSitios;
        ArrayList<GaleriaSitios> lista = new ArrayList<GaleriaSitios>();

        int[] imagenes = new int[]{R.drawable.carnet, R.drawable.carnet, R.drawable.carnet};

        String[]autores = new String[]{"Smith", "Johon", "Piedad"};

        for(int i = 0; i < imagenes.length; i++){
            galeriaSitios = new GaleriaSitios(imagenes[i],autores[i]);
            lista.add(galeriaSitios);
        }

        return lista;
    }
}
