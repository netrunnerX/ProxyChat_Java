package com.example.user.proxychat.modelos;

import java.util.List;

/**
 * Created by Saul Castillo Forte on 07/04/2017.
 */

public class Mensaje {
    private String emisor;
    private String idEmisor;
    private List<String> receptores;
    private List<String> idReceptores;
    private String mensaje;
    private int tipoMensaje;
    private String horaMensaje;

    public Mensaje() {
    }

    public Mensaje(String emisor, String idEmisor, List<String> receptores, List<String> idReceptores,
                   String mensaje, int tipoMensaje, String horaMensaje) {
        this.emisor = emisor;
        this.idEmisor = idEmisor;
        this.receptores = receptores;
        this.idReceptores = idReceptores;
        this.mensaje = mensaje;
        this.tipoMensaje = tipoMensaje;
        this.horaMensaje = horaMensaje;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String nombre) {
        this.emisor = nombre;
    }

    public List<String> getReceptores() {
        return receptores;
    }

    public void setReceptores(List<String> receptores) {
        this.receptores = receptores;
    }

    public String getIdEmisor() {
        return idEmisor;
    }

    public void setIdEmisor(String idEmisor) {
        this.idEmisor = idEmisor;
    }

    public List<String> getIdReceptores() {
        return idReceptores;
    }

    public void setIdReceptores(List<String> idReceptores) {
        this.idReceptores = idReceptores;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public int getTipoMensaje() {
        return tipoMensaje;
    }

    public void setTipoMensaje(int tipoMensaje) {
        this.tipoMensaje = tipoMensaje;
    }

    public String getHoraMensaje() {
        return horaMensaje;
    }

    public void setHoraMensaje(String horaMensaje) {
        this.horaMensaje = horaMensaje;
    }
}
