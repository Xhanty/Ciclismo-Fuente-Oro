package com.fuenteoro.ciclismo.Models;

public class Sitios {
    private String nombre, detalle;
    private double latitud_sitio, longitud_sitio;
    private String imagen;
    private int calificacion;

    public Sitios(){

    }

    public Sitios(String nombre, String detalle, double latitud_sitio, double longitud_sitio, String imagen, int calificacion) {
        this.nombre = nombre;
        this.detalle = detalle;
        this.latitud_sitio = latitud_sitio;
        this.longitud_sitio = longitud_sitio;
        this.imagen = imagen;
        this.calificacion = calificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public double getLatitud_sitio() {
        return latitud_sitio;
    }

    public void setLatitud_sitio(double latitud_sitio) {
        this.latitud_sitio = latitud_sitio;
    }

    public double getLongitud_sitio() {
        return longitud_sitio;
    }

    public void setLongitud_sitio(double longitud_sitio) {
        this.longitud_sitio = longitud_sitio;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }
}
