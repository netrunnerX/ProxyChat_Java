package com.example.user.proxychat.data;

/**
 * Created by Saul Castillo Forte on 22/04/17.
 */

public class Conversacion {

    private String contacto;
    private String idContacto;
    private String ultimoMensaje;

    public Conversacion() {
    }

    public Conversacion(String contacto, String idContacto, String ultimoMensaje) {
        this.contacto = contacto;
        this.idContacto = idContacto;
        this.ultimoMensaje = ultimoMensaje;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getIdContacto() {
        return idContacto;
    }

    public void setIdContacto(String idContacto) {
        this.idContacto = idContacto;
    }

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    public void setUltimoMensaje(String ultimoMensaje) {
        this.ultimoMensaje = ultimoMensaje;
    }
}
