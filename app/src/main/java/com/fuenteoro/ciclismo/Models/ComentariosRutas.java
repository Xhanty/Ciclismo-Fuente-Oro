package com.fuenteoro.ciclismo.Models;

public class ComentariosRutas {

    private String ciclista, comentario, fecha;

    public ComentariosRutas(){

    }

    public ComentariosRutas(String ciclista, String comentario, String fecha) {
        this.ciclista = ciclista;
        this.comentario = comentario;
        this.fecha = fecha;
    }

    public String getCiclista() {
        return ciclista;
    }

    public void setCiclista(String ciclista) {
        this.ciclista = ciclista;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
