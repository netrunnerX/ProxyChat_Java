package com.example.user.proxychat.presenter;

import com.example.user.proxychat.data.Usuario;
import com.example.user.proxychat.interactor.InvitacionesViewHolderInteractor;

/**
 * Created by net on 16/11/17.
 */

public class InvitacionesViewHolderPresenter {

    private InvitacionesViewHolderInteractor interactor;
    private InvitacionesViewHolderView view;

    public InvitacionesViewHolderPresenter(InvitacionesViewHolderView view) {
        this.view = view;
        interactor = new InvitacionesViewHolderInteractor(this);
    }

    public void obtenerContacto(String contactoId) {
        interactor.obtenerContacto(contactoId);
    }

    public void mostrarDatosContacto(Usuario contacto) {
        if (view != null)
            view.mostrarDatosContacto(contacto);
    }

    public void aceptarInvitacion(String usuarioId, String contactoId) {
        interactor.aceptarInvitacion(usuarioId, contactoId);
    }

    public void mostrarMensaje(String mensaje) {
        if (view != null)
            view.mostrarMensaje(mensaje);
    }

    public interface InvitacionesViewHolderView {
        void mostrarDatosContacto(Usuario contacto);
        void mostrarMensaje(String mensaje);
    }
}
