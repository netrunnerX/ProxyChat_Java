package com.example.user.proxychat.presenter;

import android.content.Context;
import android.net.Uri;

import com.example.user.proxychat.data.Usuario;
import com.example.user.proxychat.interactor.PerfilInteractor;

import java.io.InputStream;

/**
 * Created by net on 10/11/17.
 */

public class PerfilPresenter {

    private PerfilInteractor interactor;
    private PerfilView view;

    public PerfilPresenter(PerfilView view) {
        this.view = view;
        interactor = new PerfilInteractor(this);
    }

    public void cargarFotoPerfil(Uri imagenUrl) {
        if (view != null)
            view.cargarFotoPerfil(imagenUrl);
    }

    public void subirImagen(InputStream inputStream, Usuario usuario) {
        interactor.subirImagen(inputStream, usuario);
    }

    public void mostrarMensaje(String mensaje) {
        if (view != null)
            view.mostrarMensaje(mensaje);
    }

    public interface PerfilView {
        void cargarFotoPerfil(Uri imagenUrl);
        void mostrarMensaje(String mensaje);
    }
}
