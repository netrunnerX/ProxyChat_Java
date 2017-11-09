package com.example.user.proxychat.presenter;

import com.example.user.proxychat.interactor.InfoUsuarioInteractor;

/**
 * Created by net on 9/11/17.
 */

public class InfoUsuarioPresenter {

    private InfoUsuarioInteractor interactor;
    private InfoUsuarioView view;

    public InfoUsuarioPresenter(InfoUsuarioView view) {
        this.view = view;
        interactor = new InfoUsuarioInteractor(this);
    }

    public void mostrarMensaje(String mensaje) {
        if (view != null)
            view.mostrarMensaje(mensaje);
    }

    public void agregarContacto(String usuarioId, String contactoId) {
        interactor.agregarContacto(usuarioId, contactoId);
    }


    public interface InfoUsuarioView {
        void mostrarMensaje(String mensaje);
    }
}
