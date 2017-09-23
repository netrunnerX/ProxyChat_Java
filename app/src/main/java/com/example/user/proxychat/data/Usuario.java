package com.example.user.proxychat.data;

import java.io.Serializable;

/**
 * Created by Saul Castillo Forte on 09/04/2017.
 */

public class Usuario implements Serializable {

    private String id;
    private String apodo;
    private String imagenUrl;

    public Usuario() {
    }

    public Usuario(String id, String apodo) {

        this.id = id;
        this.apodo = apodo;
    }

    public String getApodo() {
        return apodo;
    }

    public void setApodo(String apodo) {
        this.apodo = apodo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

}
