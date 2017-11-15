package com.example.user.proxychat.presenter;

import com.example.user.proxychat.data.Usuario;
import com.example.user.proxychat.interactor.RegistroInteractor;

import java.io.InputStream;

/**
 * Created by net on 15/11/17.
 */

public class RegistroPresenter {

    private RegistroInteractor interactor;
    private RegistroView view;

    public RegistroPresenter(RegistroView view) {
        this.view = view;
        interactor = new RegistroInteractor(this);
    }

    public void registrar() {
        interactor.registrar();
    }

    public void setApodoError() {
        if (view != null)
            view.setApodoError();
    }

    public void mostrarProgressDialog() {
        if (view != null)
            view.mostrarProgressDialog();
    }

    public void ocultarProgressDialog() {
        if (view != null)
            view.ocultarProgressDialog();
    }

    public void mostrarMensaje(String mensaje) {
        if (view != null)
            view.mostrarMensaje(mensaje);
    }

    public void iniciarActividadPrincipal(Usuario usuario) {
        if (view != null)
            view.iniciarActividadPrincipal(usuario);
    }

    public void setCampos(String apodo, String email, String password) {
        interactor.setCampos(apodo, email, password);
    }

    public InputStream getImagenPerfilStream() {
        if (view != null)
            return view.getImagenPerfilStream();
        else
            return null;
    }

    public interface RegistroView {
        void mostrarMensaje(String mensaje);
        void setApodoError();
        void mostrarProgressDialog();
        void ocultarProgressDialog();
        void iniciarActividadPrincipal(Usuario usuario);
        InputStream getImagenPerfilStream();
    }
}
