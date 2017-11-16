package com.example.user.proxychat.presenter;

import com.example.user.proxychat.interactor.ConversacionesViewHolderInteractor;

/**
 * Created by net on 16/11/17.
 */

public class ConversacionesViewHolderPresenter {

    private ConversacionesViewHolderView view;
    private ConversacionesViewHolderInteractor interactor;

    public ConversacionesViewHolderPresenter(ConversacionesViewHolderView view) {
        this.view = view;
        interactor = new ConversacionesViewHolderInteractor(this);
    }

    public void consultarImagenContacto(String contactoId) {
        interactor.consultarImagenContacto(contactoId);
    }

    public void cargarImagenContacto(String imagenUrl) {
        if (view != null)
            view.cargarImagenContacto(imagenUrl);
    }

    public interface ConversacionesViewHolderView {
        void cargarImagenContacto(String imagenUrl);
    }
}
