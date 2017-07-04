package com.example.user.proxychat.modelos;

import java.io.Serializable;

/**
 * Created by Saul Castillo Forte on 20/05/17.
 */

public class MeetingPoint implements Serializable {
    private String id;
    private String idPropietario;
    private String nombre;
    private String descripcion;

    public MeetingPoint() {
    }

    public MeetingPoint(String id, String idPropietario, String nombre, String descripcion) {
        this.id = id;
        this.idPropietario = idPropietario;
        this.nombre = nombre;
        this.descripcion = descripcion;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdPropietario() {
        return idPropietario;
    }

    public void setIdPropietario(String idPropietario) {
        this.idPropietario = idPropietario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
