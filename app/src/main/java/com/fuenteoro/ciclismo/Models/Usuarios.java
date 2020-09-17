package com.fuenteoro.ciclismo.Models;

public class Usuarios {

    String nombres;
    String perfil;
    String email;
    Long telefono;

    public Usuarios(){
    }

    public Usuarios(String nombres, String perfil, String email, Long telefono) {
        this.nombres = nombres;
        this.perfil = perfil;
        this.email = email;
        this.telefono = telefono;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getTelefono() {
        return telefono;
    }

    public void setTelefono(Long telefono) {
        this.telefono = telefono;
    }
}
