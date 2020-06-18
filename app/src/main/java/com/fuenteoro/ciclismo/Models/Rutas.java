package com.fuenteoro.ciclismo.Models;

public class Rutas {
    private String nombre, detalle;
    private Long latitud_origen, longitud_origen;
    private Long latitud_destino, longitud_destino;
    private String imagen;
    private int calificacion;

    public Rutas(String nombre, String detalle, Long latitud_origen, Long longitud_origen, Long latitud_destino, Long longitud_destino, String imagen, int calificacion) {
        this.nombre = nombre;
        this.detalle = detalle;
        this.latitud_origen = latitud_origen;
        this.longitud_origen = longitud_origen;
        this.latitud_destino = latitud_destino;
        this.longitud_destino = longitud_destino;
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

    public Long getLatitud_origen() {
        return latitud_origen;
    }

    public void setLatitud_origen(Long latitud_origen) {
        this.latitud_origen = latitud_origen;
    }

    public Long getLongitud_origen() {
        return longitud_origen;
    }

    public void setLongitud_origen(Long longitud_origen) {
        this.longitud_origen = longitud_origen;
    }

    public Long getLatitud_destino() {
        return latitud_destino;
    }

    public void setLatitud_destino(Long latitud_destino) {
        this.latitud_destino = latitud_destino;
    }

    public Long getLongitud_destino() {
        return longitud_destino;
    }

    public void setLongitud_destino(Long longitud_destino) {
        this.longitud_destino = longitud_destino;
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

    public Rutas(){

    }
}
